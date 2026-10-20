package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;

/**
 * 毒师物品共用的职业 / 调试 / 目标校验。
 *
 * <p>正常生存玩家必须真的是毒师才能使用毒针、催化剂和毒气弹；
 * 创造或旁观玩家作为调试例外，可以绕过职业检查和冷却，但仍然只能对存活玩家下毒。
 * 这样调试时不用先 /setRole，也不会把“对死者下毒”这种非法操作放进来。</p>
 */
public final class PoisonerItemSupport {
    private PoisonerItemSupport() {
    }

    /** 正常生存玩家要求毒师职业；创造 / 旁观调试例外直接放行。 */
    public static boolean canUse(PlayerEntity user) {
        if (GameFunctions.isPlayerSpectatingOrCreative(user)) {
            return true;
        }
        return GameWorldComponent.KEY.get(user.getWorld()).isRole(user, FangRolesRoleRegistry.POISONER);
    }

    /** 创造 / 旁观调试时跳过物品冷却，并且不要反过来给它们写冷却。 */
    public static boolean bypassCooldown(PlayerEntity user) {
        return GameFunctions.isPlayerSpectatingOrCreative(user);
    }

    /** 下毒目标必须是 Wathe 定义的“存活玩家”，与使用者是否存活无关。 */
    public static boolean canTarget(PlayerEntity target) {
        return target != null && GameFunctions.isPlayerAliveAndSurvival(target);
    }
}
