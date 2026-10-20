package org.agmas.fangroles.mixin.roles.poisoner;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.agmas.fangroles.roles.poisoner.PoisonerConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * 毒师“每让一个玩家新中毒奖励 100 金币”的窄 mixin。
 *
 * <p>当前 Wathe 没有 PlayerPoisoned 公开事件，而所有毒源最终都会走
 * {@link PlayerPoisonComponent#setDetailedPoisonTicks(int, UUID, Identifier, NbtCompound)}。
 * 因此只在 HEAD 读取旧 poisonTicks：旧值 &gt; 0 表示目标本来就在中毒，不重复奖励；
 * 旧值 &lt;= 0 表示首次中毒或解毒后再次中毒，符合用户要求的“每次新中毒”。</p>
 */
@Mixin(PlayerPoisonComponent.class)
public abstract class PlayerPoisonComponentRewardMixin {
    @Shadow @Final private PlayerEntity player;
    @Shadow public int poisonTicks;

    @Inject(method = "setDetailedPoisonTicks", at = @At("HEAD"))
    private void fangroles$rewardPoisoner(int ticks, @Nullable UUID poisoner, @NotNull Identifier source,
                                          @Nullable NbtCompound extra, CallbackInfo ci) {
        if (this.player.getWorld().isClient()) {
            return;
        }
        if (ticks <= 0 || this.poisonTicks > 0) {
            return;
        }
        if (GameFunctions.isPlayerSpectatingOrCreative(this.player)) {
            return;
        }
        if (!(this.player instanceof ServerPlayerEntity serverVictim)) {
            return;
        }
        if (poisoner == null || poisoner.equals(this.player.getUuid())) {
            return;
        }

        ServerPlayerEntity poisonerPlayer = serverVictim.getServer().getPlayerManager().getPlayer(poisoner);
        if (poisonerPlayer == null || !GameFunctions.isPlayerAliveAndSurvival(poisonerPlayer)) {
            return;
        }

        GameWorldComponent gameWorld = GameWorldComponent.KEY.get(poisonerPlayer.getWorld());
        if (!gameWorld.isRole(poisonerPlayer, FangRolesRoleRegistry.POISONER)) {
            return;
        }

        PlayerShopComponent.KEY.get(poisonerPlayer).addToBalance(PoisonerConstants.POISON_REWARD_COINS);
        Text victimName = this.player.getDisplayName() != null
                ? this.player.getDisplayName()
                : this.player.getName();
        poisonerPlayer.sendMessage(Text.translatable(
                "tip.fangroles.poisoner.reward",
                victimName,
                PoisonerConstants.POISON_REWARD_COINS
        ).withColor(PoisonerConstants.ROLE_COLOR), true);
    }
}
