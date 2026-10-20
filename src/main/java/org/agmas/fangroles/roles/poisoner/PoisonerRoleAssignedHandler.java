package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.api.Role;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;

/**
 * 毒师分配后的初始化。
 *
 * <p>NoellesRolesspark 让毒针带一个开局冷却；这里保留同样节奏，但用户要求改成 30 秒。
 * 只清理并重设毒针冷却，不动其它物品，避免同局调试时误清别人设置的冷却。</p>
 */
public final class PoisonerRoleAssignedHandler {
    private PoisonerRoleAssignedHandler() {
    }

    public static void onRoleAssigned(PlayerEntity player, Role role) {
        if (role != FangRolesRoleRegistry.POISONER) {
            return;
        }

        player.getItemCooldownManager().remove(ModItems.POISON_NEEDLE);
        player.getItemCooldownManager().set(ModItems.POISON_NEEDLE, PoisonerConstants.START_COOLDOWN_TICKS);
    }
}
