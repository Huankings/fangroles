package org.agmas.fangroles.item;

import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;
import org.agmas.fangroles.roles.poisoner.PoisonerItemSupport;

/**
 * 毒针：右键存活玩家注入毒素，可重复使用。
 *
 * <p>毒针的“中毒”回放直接复用 Wathe 的 replay.poisoned，不再额外写毒针专用中毒文案；
 * 这里只额外记录一次 item_use，让回放能看到“谁用毒针刺了谁”。</p>
 */
public class PoisonNeedleItem extends Item {
    public PoisonNeedleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();
        if (!(entity instanceof PlayerEntity target) || target == user) {
            return ActionResult.PASS;
        }

        if (!PoisonerItemSupport.canUse(user) || !PoisonerItemSupport.canTarget(target)) {
            return ActionResult.PASS;
        }

        boolean bypassCooldown = PoisonerItemSupport.bypassCooldown(user);
        if (!bypassCooldown && user.getItemCooldownManager().isCoolingDown(this)) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            PlayerPoisonComponent poison = PlayerPoisonComponent.KEY.get(target);
            int poisonTicks = poison.poisonTicks > 0
                    ? Math.max(1, poison.poisonTicks - PoisonerConstants.POISON_NEEDLE_REDUCTION_TICKS)
                    : PoisonerConstants.POISON_NEEDLE_POISON_TICKS;

            /*
             * source 固定用 wathe:poison，让毒发死亡仍走 Wathe 默认的毒杀者回放链路；
             * 毒针不需要自定义 PLAYER_POISONED 文案，extra 传 null 即可复用 replay.poisoned。
             */
            poison.setDetailedPoisonTicks(poisonTicks, user.getUuid(), GameConstants.DeathReasons.POISON, null);

            if (!bypassCooldown) {
                user.getItemCooldownManager().set(this, PoisonerConstants.POISON_NEEDLE_COOLDOWN_TICKS);
            }

            if (user instanceof ServerPlayerEntity serverUser
                    && target instanceof ServerPlayerEntity serverTarget) {
                GameRecordManager.recordItemUse(serverUser, Registries.ITEM.getId(this), serverTarget, null);
            }
        }

        return ActionResult.SUCCESS;
    }
}
