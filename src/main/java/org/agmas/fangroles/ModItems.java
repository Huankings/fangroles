package org.agmas.fangroles;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的物品注册入口。
 *
 * <h2>注册写法</h2>
 * <pre>{@code
 * public static final Item VANISHER_DAGGER = register("vanisher_dagger",
 *         new Item(new Item.Settings().maxCount(1)));
 *
 * private static Item register(String path, Item item) {
 *     return Registry.register(Registries.ITEM, FangRolesCore.id(path), item);
 * }
 * }</pre>
 *
 * <h2>物品实现放在哪</h2>
 * <p>带行为的物品类放 {@code item/} 包下（例如
 * {@code item/VanisherDaggerItem.java}），本类只负责实例化与注册。
 * 不要把业务逻辑写在本类里。</p>
 *
 * <h2>注册后必须同步补齐的内容</h2>
 * <ol>
 *   <li>{@code assets/fangroles/models/item/<name>.json}：物品模型</li>
 *   <li>{@code assets/fangroles/textures/item/<name>.png}：物品贴图</li>
 *   <li>{@code lang/zh_cn.json} + {@code lang/en_us.json}：
 *       {@code item.fangroles.<name>} 翻译</li>
 *   <li>默认冷却：如果物品有冷却，在物品类里设置，
 *       <b>不要</b>靠 {@code GameConstants.ITEM_COOLDOWNS * getCooldownProgress(...)}
 *       反推读秒，那会和多来源冷却冲突</li>
 *   <li>Tooltip：用客户端 {@code ItemTooltipApi.registerItem(...)} /
 *       {@code registerItems(...)} 注册标准描述与冷却读秒，
 *       <b>不要</b>注册自己的全局 {@code ItemTooltipCallback}</li>
 * </ol>
 *
 * <h2>商店商品的交付类型</h2>
 * <p>如果这个物品是商店商品，注意区分
 * {@code PurchaseType.ITEM} 与 {@code PurchaseType.ACTION}，详见
 * {@code shop/FangRolesShopBootstrap} 的说明。</p>
 */
public final class ModItems {
    private ModItems() {
    }

    /**
     * 注册所有物品。
     *
     * <p>由 {@code FangRolesBootstrap.init()} 调用。</p>
     */
    public static void init() {
        // 目前没有物品，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 物品注册完成（当前为空）");
    }
}
