package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 经济与收入 API 的总入口。
 *
 * <h2>本类覆盖的范围</h2>
 * <ul>
 *   <li><b>被动收入</b>：让非杀手职业也能持续拿到金币。</li>
 *   <li><b>任务收入</b>：完成任务时的金币结算。</li>
 *   <li><b>多货币 HUD</b>：如果需要新增货币或自定义余额显示。</li>
 * </ul>
 *
 * <h2>被动收入写法</h2>
 * <pre>{@code
 * // 让某职业拥有被动收入（最常用）
 * EconomyApi.registerPassiveIncomeRole(FangRolesRoleRegistry.SOME_ROLE);
 *
 * // 需要动态允许 / 禁止时用规则，而不是每 tick 改注册表
 * EconomyApi.registerPassiveIncomeRule(FangRolesCore.id("rule_id"), 100, ctx -> ...);
 *
 * // 需要改数值时用修正器
 * EconomyApi.registerPassiveIncomeModifier(FangRolesCore.id("modifier_id"), 100, ctx -> ...);
 * }</pre>
 * <p>最终上限由 {@code GameConstants.getPassiveMoneyAmount(faction, currentBalance, baseIncome)}
 * 统一裁剪，职业侧不要自己再截断一次，否则会出现「两边都不生效」的错觉。</p>
 *
 * <h2>任务收入写法与坑</h2>
 * <ul>
 *   <li>普通任务完成监听：{@code TaskCompletionApi.AFTER_TASK_COMPLETE}。</li>
 *   <li>「任务完成但跳过默认收入」用 {@code TaskCompletionApi.registerTaskIncomeRule(...)}
 *       返回 {@code SUPPRESS_DEFAULT_INCOME}，不要 mixin 内部方法。</li>
 *   <li>拥有杀手能力的玩家<b>不会</b>叠加旧任务金币 provider。
 *       所以「杀手也能靠任务额外拿钱」这类设计，必须像 kinssaba 一样用
 *       {@code AFTER_TASK_COMPLETE} 单独补发。</li>
 * </ul>
 *
 * <h2>任务币现状</h2>
 * <p>当前 Wathe 自改版默认商店已恢复为纯金币：
 * {@code GameConstants.TASK_MONEY_PER_KILLER_TASK = 0}、{@code TASK_MONEY_PER_KILL = 0}，
 * 任务币 HUD 注册逻辑暂时注释暂停。相关 API 仍然保留，
 * 所以新职业<b>不要</b>完整复制默认杀手的 {@code ShopPrice}，
 * 否则将来重新启用任务币时该职业会买不起东西。详见 {@link FangRolesShopBootstrap}。</p>
 */
public final class FangRolesEconomyBootstrap {
    private FangRolesEconomyBootstrap() {
    }

    /**
     * 注册经济 / 收入 / 货币 HUD 相关规则。
     *
     * <p>新增职业时在这里调用该职业自己的 {@code *EconomyHandler.init()}，本类只做编排。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 经济 API 接入完成（当前为空）");
    }
}
