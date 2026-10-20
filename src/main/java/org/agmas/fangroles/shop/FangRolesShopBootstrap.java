package org.agmas.fangroles.shop;

import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.fangroles.roles.poisoner.PoisonerShopHandler;

/**
 * fangroles 商店注册总引导。
 *
 * <h2>统一使用 {@code ShopApi}</h2>
 * <ul>
 *   <li>完全替换某职业商店：{@code ShopApi.registerRoleShop(role, provider)}</li>
 *   <li>静态职业商店：{@code ShopApi.registerStaticRoleShop(...)}</li>
 *   <li>只追加 / 移除 / 替换默认商店条目：
 *       {@code ShopApi.registerShopModifier(id, priority, handler)}</li>
 *   <li>通用购买逻辑：{@code ShopApi.defaultPurchase(context)}</li>
 *   <li>统一购买冷却判断：{@code ShopApi.isPurchaseCooldownBlocked(...)}</li>
 * </ul>
 *
 * <h2>职责边界（很容易写错）</h2>
 * <p>扩展 provider 的 {@code purchase(...)} <b>只</b>负责判断商品是否交付成功。
 * 扣钱、失败提示、通用购买音效和通用购买回放由
 * {@code PlayerShopComponent#tryBuy(...)} 统一处理，
 * 扩展里<b>不要</b>自己写一遍，否则会出现扣两次钱或回放重复的问题。</p>
 *
 * <h2>交付类型必须选对</h2>
 * <ul>
 *   <li>普通 {@code new ShopEntry(...)}、{@code giveToInventory(...)}、
 *       {@code directToHotbar(...)} 都是 {@code PurchaseType.ITEM}。</li>
 *   <li>疯魔、停电、恢复电力、刷新冷却这类「购买后立即执行能力且不给显示物品」的商品，
 *       必须用 {@code ShopEntry.action(...)}，类型为 {@code PurchaseType.ACTION}。</li>
 * </ul>
 * <p>这个区分有实际后果：{@code /wathe:shopCooldownBypass <true|false>} 默认关闭；
 * 开启后<b>只有</b> {@code ITEM} 可以在商品物品冷却期间继续购买，
 * {@code ACTION} 始终受冷却限制。选错类型会让绕过开关行为异常。</p>
 *
 * <h2>非杀手职业不要用默认快捷栏交付</h2>
 * <p>默认交付逻辑只适合杀手快捷栏。非杀手职业商店请优先使用
 * {@code ShopEntry.giveToInventory(...)}、{@code ShopEntry.directToHotbar(...)}、
 * {@code ShopEntry.action(...)}，或扩展自己的 provider 交付逻辑。</p>
 *
 * <h2>多货币价格写法</h2>
 * <ul>
 *   <li>纯金币：{@code new ShopEntry(stack, int, type)} 或 {@code ShopPrice.money(int)}</li>
 *   <li>多货币同时支付：
 *       {@code ShopPrice.allOf(CurrencyAmount.money(...), CurrencyAmount.taskMoney(...))}</li>
 *   <li>多方案任选：{@code ShopPrice.anyOf(ShopPrice.option(...), ShopPrice.option(...))}</li>
 *   <li>读取默认第 0 组金币：{@code ShopApi.getDefaultMoneyPrice(item, 0, fallback)}</li>
 *   <li>读取某组某货币：
 *       {@code ShopApi.getDefaultCurrencyPrice(item, optionIndex, currencyId, fallback)}</li>
 *   <li>只有明确要完整继承默认价格结构时，才用 {@code ShopApi.getDefaultShopPrice(item)}</li>
 * </ul>
 *
 * <p><b>重要</b>：中立 / 平民职业通常<b>只能</b>读默认商品的金币价格，
 * 不要完整复制默认杀手 {@code ShopPrice}。
 * 否则将来 Wathe 重新启用任务币时，这些职业会因为价格里带有任务币而买不起东西。</p>
 */
public final class FangRolesShopBootstrap {
    private FangRolesShopBootstrap() {
    }

    /**
     * 注册所有职业商店和商店改写规则。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>ShopHandler.init()}，
     * 或者调用 {@code FangRolesShops} 里的交付逻辑。</p>
     */
    public static void init() {
        PoisonerShopHandler.init();
        FangRolesCore.LOGGER.debug("[fangroles] 商店注册完成");
    }
}
