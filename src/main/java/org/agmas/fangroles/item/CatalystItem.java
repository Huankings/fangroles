package org.agmas.fangroles.item;

import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;
import org.agmas.fangroles.roles.poisoner.PoisonerItemSupport;

import java.util.UUID;

/**
 * 催化剂：把已中毒玩家压缩到 5 秒毒发，同时重置毒针冷却。
 *
 * <p>对空气或未中毒玩家使用也会重置毒针冷却并消耗催化剂，这是 NoellesRolesspark 的原行为。
 * 只有真正催化了中毒玩家时才写入 PLAYER_POISONED 的 catalyst 来源，回放才会多出一条“催化剂催发”。</p>
 */
public class CatalystItem extends Item {
    public CatalystItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();
        if (!(entity instanceof PlayerEntity target) || !PoisonerItemSupport.canUse(user)
                || !PoisonerItemSupport.canTarget(target)) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            PlayerPoisonComponent poison = PlayerPoisonComponent.KEY.get(target);
            if (poison.poisonTicks > 0) {
                /*
                 * 开关开启时沿用原版：催化剂使用者接手毒杀归属；
                 * 关闭时保留目标原本的 poisoner，只有原本没有归属才退回使用者。
                 */
                UUID poisoner = PoisonerConstants.CATALYST_OVERRIDE_POISONER || poison.poisoner == null
                        ? user.getUuid()
                        : poison.poisoner;

                NbtCompound extra = new NbtCompound();
                extra.putString(PoisonerConstants.POISON_SOURCE_EXTRA_KEY,
                        FangRolesEventIds.POISON_SOURCE_CATALYST.toString());
                poison.setDetailedPoisonTicks(
                        PoisonerConstants.CATALYST_POISON_TICKS,
                        poisoner,
                        GameConstants.DeathReasons.POISON,
                        extra
                );
            }

            // 无论目标是否真的中毒，都重置毒针冷却并消耗一个催化剂。
            user.getItemCooldownManager().remove(ModItems.POISON_NEEDLE);
            stack.decrementUnlessCreative(1, user);

            if (user instanceof ServerPlayerEntity serverUser
                    && target instanceof ServerPlayerEntity serverTarget) {
                GameRecordManager.recordItemUse(serverUser, Registries.ITEM.getId(this), serverTarget, null);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!PoisonerItemSupport.canUse(user)) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            /*
             * 对空气使用只重置毒针冷却并消耗催化剂；
             * 这条路径不写 PLAYER_POISONED，所以不会出现“催化剂催发”回放。
             */
            user.getItemCooldownManager().remove(ModItems.POISON_NEEDLE);
            stack.decrementUnlessCreative(1, user);

            if (user instanceof ServerPlayerEntity serverUser) {
                GameRecordManager.recordItemUse(serverUser, Registries.ITEM.getId(this), null, null);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
