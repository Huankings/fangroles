package org.agmas.fangroles.roles.poisoner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.noellesroles.api.timekeeper.TimekeeperSnapshotParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 毒师在时停者回溯里的非内置状态。
 *
 * <p>Wathe 的 PlayerPoisonComponent、PlayerShopComponent 和物品冷却已经由
 * TimekeeperSnapshots 内置快照覆盖，不需要重复注册。这里只补两件事：</p>
 * <ol>
 *   <li>毒气云 / 飞行中毒气弹是自定义实体，不在时停者内置的玩家/尸体/掉落物快照里；</li>
 *   <li>PoisonerGasPlayerComponent 是自定义玩家组件，但 writeToNbt 故意不写玩家存档，
 *       不能直接通过 registerPlayerComponent 交给 CCA NBT 快照。</li>
 * </ol>
 *
 * <p>因此统一用 TimekeeperSnapshotParticipant：世界状态保存自定义实体 NBT，
 * 玩家状态保存毒气云 UUID 集合。恢复时先还原实体，再还原每个玩家的集合，
 * 这样疾跑锁定、毒气覆盖和毒气云的存在时间都会回到目标历史帧。</p>
 */
public final class PoisonerTimekeeperSnapshotParticipant implements TimekeeperSnapshotParticipant {
    private static final String CLOUDS_KEY = "Clouds";
    private static final String BOMBS_KEY = "Bombs";
    private static final String UUID_KEY = "Uuid";
    private static final String DATA_KEY = "Data";

    @Override
    public @NotNull Identifier id() {
        return FangRolesCore.id("timekeeper/poisoner_state");
    }

    @Override
    public @Nullable NbtCompound captureWorldState(
            @NotNull ServerWorld world,
            @NotNull RegistryWrapper.WrapperLookup registryLookup
    ) {
        NbtCompound root = new NbtCompound();
        root.put(CLOUDS_KEY, captureEntities(world, FangRolesEntities.POISON_GAS_CLOUD));
        root.put(BOMBS_KEY, captureEntities(world, FangRolesEntities.POISON_GAS_BOMB));
        return root;
    }

    @Override
    public void restoreWorldState(
            @NotNull ServerWorld world,
            @Nullable NbtCompound data,
            @NotNull RegistryWrapper.WrapperLookup registryLookup,
            boolean syncClientState
    ) {
        // 先恢复毒气云，再恢复飞行中的毒气弹；两者都是独立实体，顺序不会互相依赖。
        restoreEntities(world, data, CLOUDS_KEY, FangRolesEntities.POISON_GAS_CLOUD);
        restoreEntities(world, data, BOMBS_KEY, FangRolesEntities.POISON_GAS_BOMB);
    }

    @Override
    public @Nullable NbtCompound capturePlayerState(
            @NotNull ServerPlayerEntity player,
            @NotNull RegistryWrapper.WrapperLookup registryLookup
    ) {
        PoisonerGasPlayerComponent gas = PoisonerGasPlayerComponent.KEY.maybeGet(player).orElse(null);
        return gas == null ? null : gas.writeTimekeeperNbt();
    }

    @Override
    public void restorePlayerState(
            @NotNull ServerPlayerEntity player,
            @Nullable NbtCompound data,
            @NotNull RegistryWrapper.WrapperLookup registryLookup,
            boolean syncClientState
    ) {
        PoisonerGasPlayerComponent gas = PoisonerGasPlayerComponent.KEY.maybeGet(player).orElse(null);
        if (gas != null) {
            gas.readTimekeeperNbt(data, syncClientState);
        }
    }

    private static <T extends Entity> @NotNull NbtList captureEntities(
            @NotNull ServerWorld world,
            @NotNull EntityType<T> type
    ) {
        NbtList list = new NbtList();
        for (T entity : world.getEntitiesByType(type, entity -> !entity.isRemoved())) {
            NbtCompound data = new NbtCompound();
            if (!entity.saveNbt(data)) {
                continue;
            }

            /*
             * 不依赖 saveNbt 里是否写 UUID：外层再存一份稳定 UUID，
             * 玩家 gas 组件恢复时才能和云实体一一对应。
             */
            NbtCompound entry = new NbtCompound();
            entry.putUuid(UUID_KEY, entity.getUuid());
            entry.put(DATA_KEY, data);
            list.add(entry);
        }
        return list;
    }

    private static <T extends Entity> void restoreEntities(
            @NotNull ServerWorld world,
            @Nullable NbtCompound data,
            @NotNull String key,
            @NotNull EntityType<T> type
    ) {
        NbtList list = data == null ? new NbtList() : data.getList(key, NbtElement.COMPOUND_TYPE);
        Map<UUID, NbtCompound> targets = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            if (!entry.containsUuid(UUID_KEY) || !entry.contains(DATA_KEY, NbtElement.COMPOUND_TYPE)) {
                continue;
            }
            targets.put(entry.getUuid(UUID_KEY), entry.getCompound(DATA_KEY));
        }

        Set<UUID> restored = new HashSet<>();
        for (T entity : world.getEntitiesByType(type, entity -> !entity.isRemoved())) {
            UUID entityUuid = entity.getUuid();
            NbtCompound target = targets.get(entityUuid);
            if (target == null) {
                // 目标历史帧里不存在的“未来实体”必须删除，否则回溯后还会继续毒人。
                entity.discard();
                continue;
            }
            entity.readNbt(target.copy());
            /*
             * readNbt 后再次压回外层 UUID，确保玩家 gas 组件里保存的云 UUID
             * 和恢复后的实体 UUID 始终一一对应，不依赖 Entity NBT 的 UUID 字段实现。
             */
            entity.setUuid(entityUuid);
            restored.add(entityUuid);
        }

        for (Map.Entry<UUID, NbtCompound> entry : targets.entrySet()) {
            if (restored.contains(entry.getKey())) {
                continue;
            }

            T entity = type.create(world);
            if (entity == null) {
                continue;
            }
            UUID targetUuid = entry.getKey();
            entity.setUuid(targetUuid);
            entity.readNbt(entry.getValue().copy());
            entity.setUuid(targetUuid);
            if (!entity.isRemoved()) {
                world.spawnEntity(entity);
            }
        }
    }
}
