package org.agmas.fangroles.victory;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 胜利规则 API 的总入口。
 *
 * <h2>VictoryApi 返回类型</h2>
 * <ul>
 *   <li>{@code VictoryResult.pass()}：本规则不参与，交给其它规则。</li>
 *   <li>{@code VictoryResult.keepRunning()}：拖住普通结算（例如「还有独立胜利者活着，先别结束」）。</li>
 *   <li>{@code VictoryResult.vanillaWin(...)}：跟随普通阵营共胜。</li>
 *   <li>{@code VictoryResult.customWin(...)}：独立胜利，写自己的赢家阵营。</li>
 *   <li>{@code VictoryApi.endGameWithCustomVictory(...)}：主动立即结束对局。</li>
 * </ul>
 *
 * <h2>新增独立胜利必须明确的 5 个问题</h2>
 * <ol>
 *   <li>只剩自己时是否独胜？</li>
 *   <li>是否允许杀手 / 乘客共胜？</li>
 *   <li>是否需要拖住 {@code TIME} 超时结算（即返回 {@code keepRunning()}）？</li>
 *   <li>已经死亡的队友 / 伴侣是否也要写进胜利阵营？</li>
 *   <li>结算页颜色和翻译 key 是什么？</li>
 * </ol>
 * <p>这 5 条不写清楚就会出现「赢了但结算页显示别的阵营」或「该结束的局卡住不结束」。</p>
 *
 * <h2>必须使用显式阵营语义</h2>
 * <p>如果胜利条件依赖「某阵营已经死亡」，
 * 必须使用 {@code Role#getFaction()} / {@code Faction} 的显式阵营，
 * <b>不要</b>用 {@code isInnocent()} / {@code canUseKiller()} 代替阵营判断。</p>
 *
 * <h2>合并到上游工程时的时间狭缝检查</h2>
 * <p>时间狭缝会把刚死亡的玩家临时拉成「特殊存活旁观」，
 * 因此在狭缝倒计时结束前，他们仍可能被
 * {@code GameFunctions.isPlayerAliveAndSurvival(...)} 和 {@code VictoryApi}
 * 当作存活玩家，从而挡住普通结算。</p>
 * <p>如果本模组将来并入 Wathe / NoellesRoles 主工程，新增以下规则时必须同步检查
 * {@code TimekeeperRiftHandler}：独立职业或词条的自定义胜利、
 * 普通阵营胜利时追加赢家的共胜规则、以及活着时返回 {@code keepRunning()} 的规则。
 * 处理原则是：排除狭缝玩家后如果只剩一个可获胜阵营，狭缝玩家应立即
 * {@code finishTimeRift()} 转回普通死亡旁观，让 Wathe 正常结算。</p>
 *
 * <p>另外：如果本模组新增了自己的「真实存活」判断（例如特殊存活状态），
 * 那么在自己内部扫描存活玩家时也要排除处于狭缝的玩家，
 * 不要把狭缝玩家当普通活人。</p>
 */
public final class FangRolesVictoryRules {
    private FangRolesVictoryRules() {
    }

    /**
     * 注册所有特殊胜利规则。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>VictoryRule.init()}，本类只做编排。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 胜利规则接入完成（当前为空）");
    }
}
