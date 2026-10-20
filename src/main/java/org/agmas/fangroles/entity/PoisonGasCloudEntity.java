package org.agmas.fangroles.entity;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;
import org.agmas.fangroles.roles.poisoner.PoisonerGasPlayerComponent;
import org.agmas.noellesroles.api.timekeeper.TimekeeperSnapshotApi;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 毒气云：BFS 扩散、范围内中毒、范围内禁止疾跑、粒子表现。
 *
 * <p>和 NoellesRolesspark 相比，这里不再用“碰撞体积 >= 0.5 视为实心”的判定，
 * 而是只把完整方块、门、活板门、栅栏门和墙视为阻挡，半砖 / 楼梯 / 栅栏 / 薄板都可绕过，
 * 避免毒气卡在不完整方块边缘无法扩散。</p>
 */
public class PoisonGasCloudEntity extends Entity {
    private final Set<BlockPos> gasBlocks = new HashSet<>();
    private final Map<UUID, Integer> exposureTicks = new HashMap<>();
    private final Set<UUID> affectedPlayers = new HashSet<>();
    private Set<BlockPos> frontier = new HashSet<>();
    private BlockPos origin;
    private UUID ownerUuid;
    private int age;
    private boolean cleaned;

    public PoisonGasCloudEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public void tick() {
        /*
         * 时停回溯播放期间，毒气云由 PoisonerTimekeeperSnapshotParticipant 每帧恢复到历史状态。
         * 如果这里继续 tick，会在历史回放过程中重新扩散或下毒，造成“画面倒回去了但毒还在往前推进”。
         * 因此只暂停服务端逻辑；最终帧结束回溯后，下一 tick 会从恢复后的 age/exposure 继续。
         */
        if (this.getWorld() instanceof ServerWorld serverWorld
                && TimekeeperSnapshotApi.isRewinding(serverWorld)) {
            return;
        }

        super.tick();
        this.age++;

        if (this.age > PoisonerConstants.GAS_LIFETIME_TICKS) {
            this.cleanupGasEffects();
            this.discard();
            return;
        }

        if (!(this.getWorld() instanceof ServerWorld world)) {
            return;
        }

        // 首次 tick 或区块重载后气体集合为空时重新找安全种子，避免落点卡在方块里。
        if (this.age == 1 || this.gasBlocks.isEmpty()) {
            if (!this.seed(world)) {
                this.cleanupGasEffects();
                this.discard();
                return;
            }
        }

        if (this.age % PoisonerConstants.GAS_SPREAD_INTERVAL_TICKS == 0 && !this.frontier.isEmpty()) {
            this.spread(world);
        }

        this.tickPlayers(world);
        this.spawnParticles(world);
    }

    /** 找到能实际容纳毒气的起点；优先当前位置，其次上方，最后小范围搜索。 */
    private boolean seed(ServerWorld world) {
        BlockPos base = this.getBlockPos();
        BlockPos seed = this.findSeed(world, base);
        if (seed == null) {
            return false;
        }

        this.origin = seed.toImmutable();
        this.gasBlocks.clear();
        this.frontier.clear();
        this.exposureTicks.clear();
        this.gasBlocks.add(this.origin);
        this.frontier.add(this.origin);
        return true;
    }

    private BlockPos findSeed(ServerWorld world, BlockPos base) {
        if (this.canGasOccupy(world, base)) {
            return base;
        }

        BlockPos above = base.up();
        if (this.canGasOccupy(world, above)) {
            return above;
        }

        for (int radius = 1; radius <= PoisonerConstants.GAS_SEED_SEARCH_RADIUS; radius++) {
            for (BlockPos pos : BlockPos.iterate(
                    base.add(-radius, -radius, -radius),
                    base.add(radius, radius, radius))) {
                if (pos.getSquaredDistance(base) > (double) radius * radius) {
                    continue;
                }
                if (this.canGasOccupy(world, pos)) {
                    return pos.toImmutable();
                }
            }
        }
        return null;
    }

    /**
     * 毒气能否占据某个方块。
     *
     * <p>无碰撞方块直接放行；完整方块阻挡；门 / 活板门 / 栅栏门 / 墙即使不是完整方块也阻挡。
     * 半砖、楼梯、栅栏、玻璃板等不完整碰撞方块允许进入，这是修复“卡在不完整方块”的关键。</p>
     */
    private boolean canGasOccupy(ServerWorld world, BlockPos pos) {
        if (!world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getCollisionShape(world, pos);
        if (shape.isEmpty()) {
            return true;
        }
        if (state.isFullCube(world, pos)) {
            return false;
        }

        Block block = state.getBlock();
        return !(block instanceof DoorBlock)
                && !(block instanceof TrapdoorBlock)
                && !(block instanceof FenceGateBlock)
                && !(block instanceof WallBlock);
    }

    /** 保留被阻挡的边缘位置，门打开后下一轮还能继续扩散。 */
    private void spread(ServerWorld world) {
        if (this.origin == null) {
            return;
        }

        double maxRadiusSq = PoisonerConstants.GAS_MAX_RADIUS * PoisonerConstants.GAS_MAX_RADIUS;
        Set<BlockPos> nextFrontier = new HashSet<>();

        for (BlockPos pos : this.frontier) {
            if (this.gasBlocks.size() >= PoisonerConstants.GAS_MAX_BLOCKS) {
                break;
            }

            boolean stillEdge = false;
            for (Direction direction : Direction.values()) {
                if (this.gasBlocks.size() >= PoisonerConstants.GAS_MAX_BLOCKS) {
                    break;
                }

                BlockPos neighbor = pos.offset(direction);
                if (this.gasBlocks.contains(neighbor)) {
                    continue;
                }
                if (neighbor.getSquaredDistance(this.origin) > maxRadiusSq) {
                    continue;
                }
                if (!this.canGasOccupy(world, neighbor)) {
                    stillEdge = true;
                    continue;
                }

                this.gasBlocks.add(neighbor);
                nextFrontier.add(neighbor);
            }

            if (stillEdge) {
                nextFrontier.add(pos);
            }
        }

        this.frontier = nextFrontier;
    }

    /**
     * 每 tick 只更新暴露计时；真正下毒每 5 秒最多一次，不会每 tick 调用 setDetailedPoisonTicks。
     */
    private void tickPlayers(ServerWorld world) {
        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(world);

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!GameFunctions.isPlayerAliveAndSurvival(player) || this.isImmune(player, gameWorld)) {
                this.removePlayer(player);
                continue;
            }

            boolean inGas = this.isInGas(player);
            PoisonerGasPlayerComponent gas = PoisonerGasPlayerComponent.KEY.get(player);
            if (!inGas) {
                gas.removeCloud(this.getUuid());
                this.affectedPlayers.remove(player.getUuid());
                this.exposureTicks.put(player.getUuid(), 0);
                continue;
            }

            gas.addCloud(this.getUuid());
            this.affectedPlayers.add(player.getUuid());

            int ticks = this.exposureTicks.getOrDefault(player.getUuid(), 0) + 1;
            if (ticks >= PoisonerConstants.GAS_EXPOSURE_THRESHOLD_TICKS) {
                this.applyPoison(player);
                ticks = 0;
            }
            this.exposureTicks.put(player.getUuid(), ticks);
        }

        // 玩家换维度 / 下线后不会再出现在 world.getPlayers()，这里补一次清理，避免组件残留。
        for (UUID uuid : new HashSet<>(this.affectedPlayers)) {
            ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
            if (player == null || player.getWorld() != world) {
                if (player != null) {
                    PoisonerGasPlayerComponent.KEY.get(player).removeCloud(this.getUuid());
                }
                this.affectedPlayers.remove(uuid);
                this.exposureTicks.remove(uuid);
            }
        }
    }

    private boolean isImmune(ServerPlayerEntity player, GameWorldComponent gameWorld) {
        return (this.ownerUuid != null && this.ownerUuid.equals(player.getUuid()))
                || gameWorld.isRole(player, FangRolesRoleRegistry.POISONER);
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.affectedPlayers.remove(player.getUuid());
        this.exposureTicks.remove(player.getUuid());
        PoisonerGasPlayerComponent.KEY.get(player).removeCloud(this.getUuid());
    }

    private void applyPoison(ServerPlayerEntity player) {
        PlayerPoisonComponent poison = PlayerPoisonComponent.KEY.get(player);
        int poisonTicks = poison.poisonTicks > 0
                ? Math.max(1, poison.poisonTicks - PoisonerConstants.GAS_POISON_REDUCTION_TICKS)
                : PoisonerConstants.GAS_POISON_TICKS;

        NbtCompound extra = new NbtCompound();
        extra.putString(PoisonerConstants.POISON_SOURCE_EXTRA_KEY,
                FangRolesEventIds.POISON_SOURCE_GAS_BOMB.toString());

        // source 固定 wathe:poison，保证死亡回放仍能拿到毒杀者；毒源类型只放在 extra 里给回放区分。
        poison.setDetailedPoisonTicks(poisonTicks, this.ownerUuid, GameConstants.DeathReasons.POISON, extra);
    }

    private boolean isInGas(ServerPlayerEntity player) {
        if (this.gasBlocks.isEmpty()) {
            return false;
        }

        Box box = player.getBoundingBox();
        for (int x = MathHelper.floor(box.minX); x <= MathHelper.floor(box.maxX); x++) {
            for (int y = MathHelper.floor(box.minY); y <= MathHelper.floor(box.maxY); y++) {
                for (int z = MathHelper.floor(box.minZ); z <= MathHelper.floor(box.maxZ); z++) {
                    if (this.gasBlocks.contains(new BlockPos(x, y, z))) {
                        return true;
                    }
                }
            }
        }

        // 躺床玩家碰撞箱完全在床方块里，额外检查头顶和四周，避免床上的毒气失效。
        if (player.isSleeping()) {
            BlockPos bedPos = player.getBlockPos();
            if (this.gasBlocks.contains(bedPos.up())) {
                return true;
            }
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (this.gasBlocks.contains(bedPos.offset(direction))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 粒子降频到每 2 tick 一次，颜色使用毒师职业色。 */
    private void spawnParticles(ServerWorld world) {
        if (this.age % PoisonerConstants.GAS_PARTICLE_INTERVAL_TICKS != 0 || this.gasBlocks.isEmpty()) {
            return;
        }

        List<BlockPos> positions = new ArrayList<>(this.gasBlocks);
        int count = PoisonerConstants.GAS_PARTICLE_COUNT_MIN
                + world.random.nextInt(PoisonerConstants.GAS_PARTICLE_COUNT_MAX
                - PoisonerConstants.GAS_PARTICLE_COUNT_MIN + 1);
        DustParticleEffect particle = new DustParticleEffect(
                new Vector3f(PoisonerConstants.GAS_PARTICLE_RED,
                        PoisonerConstants.GAS_PARTICLE_GREEN,
                        PoisonerConstants.GAS_PARTICLE_BLUE),
                PoisonerConstants.GAS_PARTICLE_SIZE
        );

        for (int i = 0; i < count; i++) {
            BlockPos pos = positions.get(world.random.nextInt(positions.size()));
            world.spawnParticles(
                    particle,
                    pos.getX() + 0.5D + world.random.nextGaussian() * 0.3D,
                    pos.getY() + 0.5D + world.random.nextGaussian() * 0.3D,
                    pos.getZ() + 0.5D + world.random.nextGaussian() * 0.3D,
                    1, 0.0D, 0.0D, 0.0D, 0.0D
            );
        }
    }

    /** 从所有受影响玩家身上移除本云，允许他们恢复疾跑。幂等，discard / 超时都会调用。 */
    private void cleanupGasEffects() {
        if (this.cleaned) {
            return;
        }
        this.cleaned = true;

        if (this.getWorld() instanceof ServerWorld world) {
            for (UUID uuid : new HashSet<>(this.affectedPlayers)) {
                ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    PoisonerGasPlayerComponent.KEY.get(player).removeCloud(this.getUuid());
                }
            }
        }

        this.affectedPlayers.clear();
        this.exposureTicks.clear();
        this.gasBlocks.clear();
        this.frontier.clear();
    }

    @Override
    public void onRemoved() {
        this.cleanupGasEffects();
        super.onRemoved();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.ownerUuid != null) {
            nbt.putUuid("OwnerUuid", this.ownerUuid);
        }
        nbt.putInt("Age", this.age);

        /*
         * 毒气云不是 CCA 组件，时停者回溯要通过 TimekeeperSnapshotParticipant 走实体 NBT。
         * 因此把毒气形状、扩散起点和暴露计时一起写进实体 NBT；正常世界存档也会带上，
         * 但毒气本身只有 30 秒生命周期，体量可控。
         */
        long[] blocks = new long[this.gasBlocks.size()];
        int index = 0;
        for (BlockPos pos : this.gasBlocks) {
            blocks[index++] = pos.asLong();
        }
        nbt.putLongArray("GasBlocks", blocks);

        if (this.origin != null) {
            nbt.putInt("OriginX", this.origin.getX());
            nbt.putInt("OriginY", this.origin.getY());
            nbt.putInt("OriginZ", this.origin.getZ());
        }

        NbtList exposure = new NbtList();
        for (Map.Entry<UUID, Integer> entry : this.exposureTicks.entrySet()) {
            NbtCompound exposureEntry = new NbtCompound();
            exposureEntry.putUuid("Player", entry.getKey());
            exposureEntry.putInt("Ticks", entry.getValue());
            exposure.add(exposureEntry);
        }
        nbt.put("Exposure", exposure);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // 目标历史帧没有 owner 时必须清空，不能沿用当前实体的旧 owner。
        this.ownerUuid = nbt.containsUuid("OwnerUuid") ? nbt.getUuid("OwnerUuid") : null;
        this.age = nbt.getInt("Age");

        this.gasBlocks.clear();
        for (long packed : nbt.getLongArray("GasBlocks")) {
            this.gasBlocks.add(BlockPos.fromLong(packed));
        }

        if (nbt.contains("OriginX") && nbt.contains("OriginY") && nbt.contains("OriginZ")) {
            this.origin = new BlockPos(nbt.getInt("OriginX"), nbt.getInt("OriginY"), nbt.getInt("OriginZ"));
        } else if (!this.gasBlocks.isEmpty()) {
            this.origin = this.gasBlocks.iterator().next();
        } else {
            this.origin = null;
        }

        // 恢复后把所有已占据方块都视为 frontier，下一轮扩散会重新检查边缘；
        // 这比额外保存 frontier 更省 NBT，也不会漏掉门打开后的继续扩散。
        this.frontier = new HashSet<>(this.gasBlocks);

        this.exposureTicks.clear();
        if (nbt.contains("Exposure", NbtElement.LIST_TYPE)) {
            NbtList exposure = nbt.getList("Exposure", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < exposure.size(); i++) {
                NbtCompound exposureEntry = exposure.getCompound(i);
                if (exposureEntry.containsUuid("Player")) {
                    this.exposureTicks.put(exposureEntry.getUuid("Player"), exposureEntry.getInt("Ticks"));
                }
            }
        }

        this.affectedPlayers.clear();
        this.cleaned = false;
    }
}
