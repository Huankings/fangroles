package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.api.sync.PlayerComponentSyncApi;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.agmas.fangroles.sync.FangRolesPlayerComponentSyncHandlers;

/**
 * 毒师读取目标真实中毒状态所需的选择性观察者规则。
 *
 * <p>Wathe 的 PlayerPoisonComponent 默认只同步给中毒者本人。毒师客户端要做被动透视，
 * 必须通过 PlayerComponentSyncApi 额外授权，而不是把组件改回全员同步或每 tick 广播。
 * 授权在服务端按真实职业判断，死亡 / 停局 / 换职后 API 会撤权并发送空 NBT 清缓存。</p>
 */
public final class PoisonerComponentSyncHandler {
    private static boolean initialized;

    private PoisonerComponentSyncHandler() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        PlayerComponentSyncApi.registerObserverRule(
                FangRolesEventIds.POISONER_POISON_SYNC_ID,
                PlayerPoisonComponent.KEY,
                PlayerComponentSyncApi.DEFAULT_PRIORITY,
                context -> {
                    if (!GameFunctions.isPlayerAliveAndSurvival(context.recipient())) {
                        return false;
                    }
                    GameWorldComponent gameWorld = GameWorldComponent.KEY.get(context.recipient().getWorld());
                    return gameWorld.isRunning()
                            && gameWorld.isRole(context.recipient(), FangRolesRoleRegistry.POISONER);
                }
        );

        /*
         * 局中 /setRole 转职为毒师时，目标的中毒组件可能没有刚好发生变化。
         * 这里注册统一刷新回调，由 FangRolesRoleAssignedBootstrap 在分配完成后触发，
         * 让新毒师立即拿到当前追踪目标的中毒状态，不必等下一次 20 tick 权限检查。
         */
        FangRolesPlayerComponentSyncHandlers.registerRoleAssignedRefresh(player ->
                PlayerComponentSyncApi.syncTrackedToRecipient(PlayerPoisonComponent.KEY, player));
    }
}
