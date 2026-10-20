package org.agmas.fangroles.roles.poisoner;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.fangroles.registry.FangRolesCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 玩家当前所处的毒气云集合。
 *
 * <p>这个组件只同步给本人：客户端只需要知道自己是否在毒气里，用来阻止疾跑；
 * 不需要知道别人的毒气位置。进入 / 离开只在集合从空变非空、或从非空变空时 sync，
 * 不会每 tick 发包。</p>
 */
public class PoisonerGasPlayerComponent implements AutoSyncedComponent {
    public static final ComponentKey<PoisonerGasPlayerComponent> KEY =
            ComponentRegistry.getOrCreate(FangRolesCore.id("poisoner_gas"), PoisonerGasPlayerComponent.class);

    private final PlayerEntity player;
    private final Set<UUID> gasClouds = new HashSet<>();

    public PoisonerGasPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public boolean isInGas() {
        return !this.gasClouds.isEmpty();
    }

    public void sync() {
        if (!this.player.getWorld().isClient()) {
            KEY.sync(this.player);
        }
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity recipient) {
        return recipient.equals(this.player);
    }

    /** 进入一朵毒气云；只有“从不在毒气到在毒气”的边界才停疾跑并同步。 */
    public void addCloud(UUID cloudUuid) {
        boolean wasInGas = this.isInGas();
        if (!this.gasClouds.add(cloudUuid)) {
            return;
        }
        if (!wasInGas) {
            if (this.player.isSprinting()) {
                this.player.setSprinting(false);
            }
            this.sync();
        }
    }

    /** 离开一朵毒气云；若还在其它云里则保持 isInGas=true，不重复同步。 */
    public void removeCloud(UUID cloudUuid) {
        boolean wasInGas = this.isInGas();
        if (!this.gasClouds.remove(cloudUuid)) {
            return;
        }
        if (wasInGas && !this.isInGas()) {
            this.sync();
        }
    }

    /** 回合重置 / 死亡清理；清空后允许玩家重新疾跑。 */
    public void reset() {
        boolean wasInGas = this.isInGas();
        this.gasClouds.clear();
        if (wasInGas) {
            this.sync();
        }
    }

    /**
     * 时停者回溯用的玩家状态快照。
     *
     * <p>毒气云实体本身由 PoisonerTimekeeperSnapshotParticipant 承担；玩家这里只保存
     * “当前在哪几朵云里”的 UUID。不能直接复用 writeToNbt：组件默认不写玩家存档，
     * 否则玩家在毒气里退出后，下一局可能残留一个永远等不到云消散的 isInGas 状态。</p>
     */
    public NbtCompound writeTimekeeperNbt() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        for (UUID uuid : this.gasClouds) {
            list.add(NbtString.of(uuid.toString()));
        }
        tag.put("GasClouds", list);
        return tag;
    }

    /**
     * 恢复时停历史帧里的毒气云集合。
     *
     * <p>只有 syncClientState 为 true（最终帧或显式中间帧）且集合内容真的变化时才 sync，
     * 避免回溯每 4 tick 都给客户端发包。恢复后如果玩家在毒气里仍处于疾跑，立即置 false。</p>
     */
    public void readTimekeeperNbt(@Nullable NbtCompound tag, boolean syncClientState) {
        this.gasClouds.clear();

        if (tag != null && tag.contains("GasClouds", NbtElement.LIST_TYPE)) {
            NbtList list = tag.getList("GasClouds", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                try {
                    this.gasClouds.add(UUID.fromString(list.getString(i)));
                } catch (IllegalArgumentException ignored) {
                    // 损坏的 UUID 直接忽略，不能让一个坏快照卡住玩家。
                }
            }
        }

        if (this.isInGas() && this.player.isSprinting()) {
            this.player.setSprinting(false);
        }
        if (syncClientState) {
            /*
             * 不能只在“本次恢复后集合变化”时 sync：中间帧可能已经把服务端集合改成 B
             * 但没有发给客户端，最终帧 B→B 不会再变化，客户端会一直停留在旧集合 A。
             * 因此最终帧（或显式开启中间帧同步的帧）无条件补发一次当前集合。
             */
            this.sync();
        }
    }

    /*
     * 毒气是局内瞬时状态，不写玩家存档；重进服务器后由仍然存在的毒气云在下一 tick 重新 add。
     * 网络同步改用下面的自定义二进制包，只发云 UUID，不发完整 NBT。
     */
    @Override
    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.gasClouds.clear();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeVarInt(this.gasClouds.size());
        for (UUID uuid : this.gasClouds) {
            buf.writeUuid(uuid);
        }
    }

    @Override
    public void applySyncPacket(RegistryByteBuf buf) {
        this.gasClouds.clear();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            this.gasClouds.add(buf.readUuid());
        }

        /*
         * 客户端收到“进入毒气”的同步时，本地可能已经因为按住疾跑键进入 sprint 状态；
         * 这里立即置 false，后续 setSprinting(true) 会被 GasSprintLockMixin 拦截。
         */
        if (this.player.getWorld().isClient() && this.isInGas() && this.player.isSprinting()) {
            this.player.setSprinting(false);
        }
    }
}
