package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.api.event.GameEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.entity.PoisonGasBombEntity;
import org.agmas.fangroles.entity.PoisonGasCloudEntity;
import org.agmas.harpymodloader.events.ResetPlayerEvent;

/**
 * 毒师回合 / 玩家重置清理。
 *
 * <p>毒气云、飞行中的毒气弹和玩家 gas 组件都是局内瞬时状态：
 * 开局前不清理会让上一局的毒气继续毒人，结算后不清理会让玩家保留“毒气中不能疾跑”的状态。
 * 冷却与组件在 ResetPlayerEvent 清，实体在世界生命周期清理。</p>
 */
public final class PoisonerResetHandler {
    private static boolean initialized;

    private PoisonerResetHandler() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        ResetPlayerEvent.EVENT.register(player -> {
            player.getItemCooldownManager().remove(ModItems.POISON_NEEDLE);
            player.getItemCooldownManager().remove(ModItems.CATALYST);
            player.getItemCooldownManager().remove(ModItems.POISON_GAS_BOMB);
            PoisonerGasPlayerComponent.KEY.get(player).reset();
        });

        GameEvents.ON_BEGIN_INITIALIZE.register((world, gameWorld) -> discardGasEntities(world));
        GameEvents.ON_FINISH_FINALIZE.register((world, gameWorld) -> {
            if (world instanceof ServerWorld serverWorld) {
                discardGasEntities(serverWorld);
            }
        });
    }

    private static void discardGasEntities(ServerWorld world) {
        for (PoisonGasCloudEntity cloud : world.getEntitiesByType(
                FangRolesEntities.POISON_GAS_CLOUD, entity -> true)) {
            cloud.discard();
        }
        for (PoisonGasBombEntity bomb : world.getEntitiesByType(
                FangRolesEntities.POISON_GAS_BOMB, entity -> true)) {
            bomb.discard();
        }

        // 实体 discard 会通过 onRemoved 移除云 UUID；这里再对所有在线玩家兜底重置一次。
        for (ServerPlayerEntity player : world.getPlayers()) {
            PoisonerGasPlayerComponent.KEY.get(player).reset();
        }
    }
}
