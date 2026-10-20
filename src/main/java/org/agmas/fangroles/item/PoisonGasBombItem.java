package org.agmas.fangroles.item;

import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.entity.PoisonGasBombEntity;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;
import org.agmas.fangroles.roles.poisoner.PoisonerItemSupport;

/**
 * 毒气弹：右键丢出，落地生成毒气云。
 *
 * <p>正常生存玩家必须是毒师；创造 / 旁观调试例外。毒气弹本身没有物品冷却，
 * 购买后可以连续投掷，和手雷一样靠库存限制数量。</p>
 */
public class PoisonGasBombItem extends Item {
    public PoisonGasBombItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!PoisonerItemSupport.canUse(user)) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            PoisonGasBombEntity bomb = new PoisonGasBombEntity(FangRolesEntities.POISON_GAS_BOMB, world);
            bomb.setOwner(user);
            bomb.setPos(user.getX(), user.getEyeY() - 0.1D, user.getZ());
            bomb.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F,
                    PoisonerConstants.POISON_GAS_BOMB_THROW_SPEED, 1.0F);
            world.spawnEntity(bomb);

            if (user instanceof ServerPlayerEntity serverUser) {
                GameRecordManager.recordItemUse(serverUser, Registries.ITEM.getId(this), null, null);
            }
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.decrementUnlessCreative(1, user);
        return TypedActionResult.success(stack, world.isClient());
    }
}
