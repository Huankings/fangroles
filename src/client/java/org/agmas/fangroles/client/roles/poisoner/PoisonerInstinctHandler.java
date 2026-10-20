package org.agmas.fangroles.client.roles.poisoner;

import dev.doctor4t.wathe.api.instinct.InstinctApi;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;

/**
 * 毒师被动透视：未开启本能时，把正在中毒的玩家显示成毒师职业色。
 *
 * <p>Wathe 默认杀手本能在开启后会按阵营 / 心情给所有目标上色；这里在开启本能的帧直接 PASS，
 * 完全交给默认本能，避免“毒师色覆盖掉原来的本能透视”。只有在未开启本能时，
 * 才把真实中毒目标单独标成毒师色。</p>
 */
public final class PoisonerInstinctHandler {
    private PoisonerInstinctHandler() {
    }

    public static void register() {
        InstinctApi.registerHighlight(
                FangRolesEventIds.POISONER_INSTINCT_ID,
                PoisonerConstants.INSTINCT_HIGHLIGHT_PRIORITY,
                (viewer, target) -> {
                    if (!(target instanceof PlayerEntity targetPlayer) || targetPlayer == viewer) {
                        return InstinctApi.HighlightResult.pass();
                    }
                    if (!GameFunctions.isPlayerAliveAndSurvival(targetPlayer)) {
                        return InstinctApi.HighlightResult.pass();
                    }
                    if (!WatheClient.isPlayerAliveAndInSurvival()) {
                        return InstinctApi.HighlightResult.pass();
                    }

                    GameWorldComponent gameWorld = GameWorldComponent.KEY.get(viewer.getWorld());
                    if (!gameWorld.isRole(viewer, FangRolesRoleRegistry.POISONER)) {
                        return InstinctApi.HighlightResult.pass();
                    }

                    // 开启本能时交给 Wathe 默认颜色，不覆盖原来的本能透视。
                    if (WatheClient.isInstinctEnabled()) {
                        return InstinctApi.HighlightResult.pass();
                    }

                    return PlayerPoisonComponent.KEY.get(targetPlayer).poisonTicks > 0
                            ? InstinctApi.HighlightResult.color(PoisonerConstants.ROLE_COLOR)
                            : InstinctApi.HighlightResult.pass();
                }
        );
    }
}
