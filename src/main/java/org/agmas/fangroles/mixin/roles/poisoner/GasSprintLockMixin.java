package org.agmas.fangroles.mixin.roles.poisoner;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.fangroles.roles.poisoner.PoisonerGasPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 毒气范围内的强制禁疾跑。
 *
 * <p>Wathe 当前没有公开的“禁止疾跑”规则 API，而只降低移动速度不能满足
 * “isSprinting() 必须为 false”的要求。这里选择注入 {@link Entity#setSprinting(boolean)}
 * 这个最窄的状态入口：只在玩家处于毒气云内时取消“设置为 true”，
 * 不修改玩家的移动速度、体力或其它状态。客户端和服务端都会执行，
 * 因此客户端不会发出 start-sprint 包，服务端也无法被重新置为疾跑。</p>
 */
@Mixin(Entity.class)
public abstract class GasSprintLockMixin {
    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void fangroles$blockSprintInGas(boolean sprinting, CallbackInfo ci) {
        if (!sprinting) {
            return;
        }
        if (!((Object) this instanceof PlayerEntity player) || player.getWorld() == null) {
            return;
        }
        /*
         * 使用 maybeGet 而不是 get：实体在极早期 tick / 客户端尚未挂载组件时不能因为
         * 组件容器还没准备好而抛异常；没有组件就等同“不在毒气里”。
         */
        PoisonerGasPlayerComponent gas = PoisonerGasPlayerComponent.KEY.maybeGet(player).orElse(null);
        if (gas != null && gas.isInGas()) {
            ci.cancel();
        }
    }
}
