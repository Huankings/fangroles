package org.agmas.fangroles.death;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 击杀 / 死亡分阶段流程的总引导。
 *
 * <h2>统一入口：{@code DeathApi}</h2>
 * <ul>
 *   <li>击杀奖励（发钱、给物品、计数）</li>
 *   <li>重复死亡吞噬（同一玩家被多次判定死亡时只结算一次）</li>
 *   <li>致死确认前的转化（例如「本该死亡但转化为特殊状态」）</li>
 *   <li>确认死亡后的清理</li>
 *   <li>尸体生成回调（{@code BodySpawnContext}）</li>
 * </ul>
 * <p><b>不要</b>把大量死亡特判堆到主类，也不要 mixin
 * {@code GameFunctions.killPlayer(...)}。</p>
 *
 * <h2>死亡保护链</h2>
 * <p>「本职业死亡时不应该真的死」这类需求走 {@code AllowPlayerDeath} 保护链，
 * 不要靠取消伤害事件来模拟。NoellesRoles 侧可以参考
 * {@code NoellesRolesDeathBootstrap} 和 {@code CommonForcedDeathHandler} 的写法。</p>
 *
 * <h2>DeathApi 优先级约定（越大越先执行）</h2>
 * <ol>
 *   <li>重复死亡保护（最高）</li>
 *   <li>特殊存活保护</li>
 *   <li>死亡流程状态</li>
 *   <li>回放上下文</li>
 *   <li>致死确认前拦截</li>
 *   <li>普通逻辑</li>
 *   <li>确认死亡后的奖励 / 二段机制</li>
 *   <li>最终清理（最低）</li>
 * </ol>
 * <p>同 priority 时<b>后注册的先执行</b>。</p>
 *
 * <h2>必须避免的两个实战坑</h2>
 * <ol>
 *   <li><b>不要</b>在 {@code beforeAttempt} 阶段按「死亡请求次数」发奖励。
 *       胶带残留、环境伤害和递归死亡会导致重复金币。
 *       奖励必须放在确认死亡阶段（{@code confirmedDeath()} 或等价的
 *       {@code afterMarkedDead} 阶段），并检查攻击者是当前真实存活玩家。</li>
 *   <li><b>不要</b>把「死亡后清理」只写成清一个布尔值。
 *       控制关系、静默状态、贴带计数、临时物品等都要成套清理，
 *       否则会出现「复活后状态还在」的问题。</li>
 * </ol>
 */
public final class FangRolesDeathBootstrap {
    private FangRolesDeathBootstrap() {
    }

    /**
     * 注册所有死亡保护、击杀奖励与确认死亡后的清理。
     *
     * <p>新增职业时在这里按阶段接入对应 handler，并写清「为什么放在这个阶段」。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 死亡 / 击杀 API 接入完成（当前为空）");
    }
}
