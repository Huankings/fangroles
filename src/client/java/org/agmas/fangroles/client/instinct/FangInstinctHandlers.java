package org.agmas.fangroles.client.instinct;

import org.agmas.fangroles.client.roles.poisoner.PoisonerInstinctHandler;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 本能透视（Instinct）注册入口。
 *
 * <p>统一走 Wathe {@code InstinctApi}，不要写自己的渲染描边逻辑。</p>
 *
 * <h2>三个入口</h2>
 * <ul>
 *   <li><b>能不能开本能</b>：{@code InstinctApi.registerAvailability(...)}
 *       —— 返回 {@code ENABLE} / {@code DISABLE} / {@code PASS}。</li>
 *   <li><b>某目标显示什么颜色 / 是否隐藏</b>：{@code InstinctApi.registerHighlight(...)}
 *       —— 返回 {@code color(...)} / {@code hide()} / {@code PASS}。</li>
 *   <li><b>全局压制本能</b>：用更高 priority 的 availability 规则返回 {@code DISABLE}。</li>
 * </ul>
 * <p>返回 {@code PASS} 表示继续交给低优先级规则和 Wathe 默认行为。
 * priority 越大越先执行。</p>
 *
 * <h2>两条重要语义</h2>
 * <ol>
 *   <li><b>本能颜色是阵营语义，不是身份泄露工具。</b>
 *       给同伙上色、给敌人保持默认，是标准做法；
 *       如果某个职业要「看到特定玩家是特殊颜色」，
 *       要在注释里写清这是玩法设计还是调试遗留。</li>
 *   <li><b>隐藏本能 ≠ 不可攻击</b>。
 *       本能只是视觉；隐藏了描边不代表目标不能被攻击，
 *       反过来也不能因为能看见描边就认为服务端一定会放行攻击。
 *       攻击合法性统一走 {@code TargetVisibilityApi} 与
 *       {@code WeaponTargetingApi}。</li>
 * </ol>
 *
 * <h2>与词条的关系</h2>
 * <p>词条<b>不会</b>出现在本能透视里（Harpy 的既有行为）。
 * 所以不要试图让词条通过本能显示，那需要改 Harpy 本体，
 * 而本模板的原则是不改上游。</p>
 *
 * <h2>文件组织</h2>
 * <p>按职业拆到 {@code client/instinct/roles/<role_id>/}，
 * 按词条拆到 {@code client/instinct/modifiers/<modifier_id>/}，
 * 本类只调用各自的 {@code register()}。</p>
 */
public final class FangInstinctHandlers {
    private FangInstinctHandlers() {
    }

    /**
     * 注册所有本能规则。
     *
     * <p>目前没有职业，空实现。</p>
     */
    public static void register() {
        PoisonerInstinctHandler.register();
        FangRolesCore.LOGGER.debug("[fangroles] 本能透视注册完成");
    }
}
