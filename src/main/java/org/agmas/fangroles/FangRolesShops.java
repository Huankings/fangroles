package org.agmas.fangroles;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的商店交付逻辑实现区。
 *
 * <p>注册编排在 {@code shop/FangRolesShopBootstrap}；本类用于容纳
 * 「购买成功后怎么把东西交到玩家手上」这类自定义交付逻辑。</p>
 *
 * <h2>什么时候需要写在这里</h2>
 * <p>Wathe 默认交付逻辑只适合杀手快捷栏（{@code directToHotbar} 风格）。
 * 非杀手职业、背包型交付、或者一次给多件随机物品时，
 * 应该在本类实现自己的交付方法，再由职业的 ShopHandler 调用。</p>
 *
 * <h2>硬性边界</h2>
 * <ul>
 *   <li>本类<b>不</b>负责扣钱、失败提示、购买音效和通用购买回放
 *       —— 这些由 {@code PlayerShopComponent#tryBuy(...)} 统一处理。</li>
 *   <li>本类的方法只返回「是否交付成功」，不要抛异常。</li>
 *   <li>需要「购买后立即执行能力、不给物品」的商品，
 *       请用 {@code ShopEntry.action(...)}（{@code PurchaseType.ACTION}），
 *       而不是给玩家一个假物品再让右键触发。</li>
 * </ul>
 *
 * <h2>背包满时的处理</h2>
 * <p>交付到背包时如果背包满，要么掉落到脚下，要么返回失败让 {@code tryBuy} 走退款 / 未扣款流程。
 * <b>不要</b>静默吞掉物品，那会导致玩家花了钱没拿到东西。</p>
 */
public final class FangRolesShops {
    private FangRolesShops() {
    }

    // 目前没有自定义交付逻辑。
    // 新增时建议写成 public static boolean giveXxx(ServerPlayerEntity player) 形式，
    // 并在方法上方用中文注释写清「交付失败时的行为」。
}
