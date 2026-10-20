package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.record.GameRecordEvent;
import dev.doctor4t.wathe.record.GameRecordManager;
import dev.doctor4t.wathe.record.GameRecordTypes;
import dev.doctor4t.wathe.record.replay.DefaultReplayFormatters;
import dev.doctor4t.wathe.record.replay.ReplayGenerator;
import dev.doctor4t.wathe.record.replay.ReplayRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * 毒师回放格式化器。
 *
 * <p>毒针只注册 item_use（用毒针刺了谁），中毒行继续复用 Wathe 的 replay.poisoned；
 * 毒气和催化剂才注册 PLAYER_POISONED 的自定义分支。催化剂只有在真正催化了已中毒玩家时
 * 才会走 setDetailedPoisonTicks，因此不会出现“没催化也显示催化剂催发”的空回放。</p>
 */
public final class PoisonerReplayFormatters {
    private PoisonerReplayFormatters() {
    }

    public static void register() {
        ReplayRegistry.registerItemUseFormatter(Registries.ITEM.getId(ModItems.POISON_NEEDLE),
                (event, match, world) -> {
                    UUID actor = actorUuid(event);
                    UUID target = targetUuid(event);
                    if (actor == null || target == null) {
                        return null;
                    }
                    return Text.translatable("replay.item_use.fangroles.poison_needle",
                            playerName(actor, match), playerName(target, match));
                });

        ReplayRegistry.registerItemUseFormatter(Registries.ITEM.getId(ModItems.POISON_GAS_BOMB),
                (event, match, world) -> {
                    UUID actor = actorUuid(event);
                    return actor == null ? null : Text.translatable(
                            "replay.item_use.fangroles.poison_gas_bomb", playerName(actor, match));
                });

        ReplayRegistry.registerItemUseFormatter(Registries.ITEM.getId(ModItems.CATALYST),
                (event, match, world) -> {
                    UUID actor = actorUuid(event);
                    if (actor == null) {
                        return null;
                    }
                    UUID target = targetUuid(event);
                    return target == null
                            ? Text.translatable("replay.item_use.fangroles.catalyst.no_target", playerName(actor, match))
                            : Text.translatable("replay.item_use.fangroles.catalyst",
                                    playerName(actor, match), playerName(target, match));
                });

        /*
         * 覆盖 PLAYER_POISONED 的全局格式化器，但只处理方括号里的 fangroles 毒源；
         * 其它毒源（床毒、普通毒药、酒保饮料等）必须回退到 Wathe 默认实现，避免破坏本体回放。
         */
        ReplayRegistry.registerFormatter(GameRecordTypes.PLAYER_POISONED, PoisonerReplayFormatters::formatPoisoned);
    }

    @Nullable
    private static Text formatPoisoned(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerWorld world) {
        Text victim = targetText(event, match);
        if (victim == null) {
            return null;
        }

        NbtCompound data = event.data();
        String kind = data.getString(PoisonerConstants.POISON_SOURCE_EXTRA_KEY);
        UUID poisoner = data.containsUuid("poisoner_uuid")
                ? data.getUuid("poisoner_uuid")
                : actorUuid(event);
        Text poisonerText = poisoner == null ? null : playerName(poisoner, match);

        if (FangRolesEventIds.POISON_SOURCE_GAS_BOMB.toString().equals(kind)) {
            return poisonerText == null
                    ? Text.translatable("replay.poisoned.fangroles.gas_bomb", victim)
                    : Text.translatable("replay.poisoned.fangroles.gas_bomb.by", victim, poisonerText);
        }
        if (FangRolesEventIds.POISON_SOURCE_CATALYST.toString().equals(kind)) {
            return poisonerText == null
                    ? Text.translatable("replay.poisoned.fangroles.catalyst", victim)
                    : Text.translatable("replay.poisoned.fangroles.catalyst.by", victim, poisonerText);
        }

        return DefaultReplayFormatters.formatPoisoned(event, match, world);
    }

    @Nullable
    private static UUID actorUuid(GameRecordEvent event) {
        return event.data().containsUuid("actor") ? event.data().getUuid("actor") : null;
    }

    @Nullable
    private static UUID targetUuid(GameRecordEvent event) {
        return event.data().containsUuid("target") ? event.data().getUuid("target") : null;
    }

    @Nullable
    private static Text targetText(GameRecordEvent event, GameRecordManager.MatchRecord match) {
        UUID uuid = targetUuid(event);
        return uuid == null ? null : playerName(uuid, match);
    }

    private static Text playerName(UUID uuid, GameRecordManager.MatchRecord match) {
        Map<UUID, ReplayGenerator.PlayerInfo> cache = ReplayGenerator.getPlayerInfoCache(match);
        return ReplayGenerator.formatPlayerName(uuid, cache);
    }
}
