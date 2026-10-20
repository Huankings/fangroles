package org.agmas.fangroles.roles.poisoner;

import dev.doctor4t.wathe.api.shop.ShopApi;
import dev.doctor4t.wathe.api.shop.ShopContext;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.item.Item;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.registry.FangRolesEventIds;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 毒师商店：在 Wathe 默认杀手商店上原地替换条目，保留原有排列顺序。
 *
 * <p>价格全部从 Wathe 默认商店读取，只有读不到时才使用 PoisonerConstants 的兜底值，
 * 这样上游调整匕首 / 手雷 / 停电价格时毒师会自动跟随。</p>
 */
public final class PoisonerShopHandler {
    private PoisonerShopHandler() {
    }

    public static void init() {
        ShopApi.registerShopModifier(
                FangRolesEventIds.POISONER_SHOP_MODIFIER_ID,
                PoisonerConstants.SHOP_MODIFIER_PRIORITY,
                PoisonerShopHandler::modifyShop
        );
    }

    private static void modifyShop(@NotNull ShopContext context, @NotNull List<ShopEntry> entries) {
        if (context.role() != FangRolesRoleRegistry.POISONER) {
            return;
        }

        int knifePrice = ShopApi.getDefaultMoneyPrice(
                WatheItems.KNIFE, 0, PoisonerConstants.FALLBACK_KNIFE_PRICE);
        int grenadePrice = ShopApi.getDefaultMoneyPrice(
                WatheItems.GRENADE, 0, PoisonerConstants.FALLBACK_GRENADE_PRICE);
        int blackoutPrice = ShopApi.getDefaultMoneyPrice(
                WatheItems.BLACKOUT, 0, PoisonerConstants.FALLBACK_BLACKOUT_PRICE);
        int poisonVialPrice = ShopApi.getDefaultMoneyPrice(
                WatheItems.POISON_VIAL, 0, PoisonerConstants.FALLBACK_POISON_VIAL_PRICE);
        int scorpionPrice = ShopApi.getDefaultMoneyPrice(
                WatheItems.SCORPION, 0, PoisonerConstants.FALLBACK_SCORPION_PRICE);

        // 匕首位置 -> 毒针，价格读取匕首默认价。
        replaceItem(entries, WatheItems.KNIFE, new ShopEntry(
                ModItems.POISON_NEEDLE.getDefaultStack(),
                knifePrice,
                ShopEntry.Type.WEAPON
        ));

        // 左轮位置 -> 催化剂，同样读取匕首默认价。
        replaceItem(entries, WatheItems.REVOLVER, new ShopEntry(
                ModItems.CATALYST.getDefaultStack(),
                knifePrice,
                ShopEntry.Type.WEAPON
        ));

        // 手雷位置 -> 毒气弹，价格 = 手雷默认价 - 115。
        replaceItem(entries, WatheItems.GRENADE, new ShopEntry(
                ModItems.POISON_GAS_BOMB.getDefaultStack(),
                Math.max(0, grenadePrice - PoisonerConstants.GAS_BOMB_PRICE_DISCOUNT),
                ShopEntry.Type.WEAPON
        ));

        // 删除疯魔模式。
        removeItem(entries, WatheItems.PSYCHO_MODE);

        // 毒药瓶 / 蝎子：保留条目，只把价格减半。
        replaceItem(entries, WatheItems.POISON_VIAL, new ShopEntry(
                WatheItems.POISON_VIAL.getDefaultStack(),
                Math.max(0, poisonVialPrice / PoisonerConstants.HALF_PRICE_DIVISOR),
                ShopEntry.Type.POISON
        ));
        replaceItem(entries, WatheItems.SCORPION, new ShopEntry(
                WatheItems.SCORPION.getDefaultStack(),
                Math.max(0, scorpionPrice / PoisonerConstants.HALF_PRICE_DIVISOR),
                ShopEntry.Type.POISON
        ));

        /*
         * 停电必须保持 ACTION 语义；如果直接 new ShopEntry(..., TOOL) 会变成一个停电图标物品，
         * 而不是购买后立即触发停电。
         */
        replaceItem(entries, WatheItems.BLACKOUT, ShopEntry.action(
                WatheItems.BLACKOUT.getDefaultStack(),
                Math.max(0, blackoutPrice - PoisonerConstants.BLACKOUT_PRICE_DISCOUNT),
                ShopEntry.Type.TOOL,
                PlayerShopComponent::useBlackout
        ));
    }

    private static void replaceItem(@NotNull List<ShopEntry> entries, @NotNull Item item, @NotNull ShopEntry replacement) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).stack().isOf(item)) {
                entries.set(i, replacement);
                return;
            }
        }
        // 上游如果移除了某个默认条目，就在末尾补一个，避免毒师商店缺项。
        entries.add(replacement);
    }
    private static void removeItem(@NotNull List<ShopEntry> entries, @NotNull Item item) {
        entries.removeIf(entry -> entry.stack().isOf(item));
    }

}
