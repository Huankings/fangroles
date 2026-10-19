package org.agmas.fangroles.client.input;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端左键预攻击接管入口。
 *
 * <p>有些职业的武器在「左键」时并不走默认攻击流程，例如：</p>
 * <ul>
 *   <li>狙击枪：左键是开镜而不是开枪</li>
 *   <li>需要蓄力的武器：左键按下开始蓄力，松开才发射</li>
 *   <li>扩展手雷：左键是切换投掷模式</li>
 * </ul>
 * <p>这类需求统一在本类接管，按职业拆到
 * {@code client/roles/<role_id>/<RoleName>PreAttackHandler}。</p>
 *
 * <h2>接管时的三条约定</h2>
 * <ol>
 *   <li><b>必须能放行</b>：不是本职业 / 本物品时，
 *       要明确返回「未处理」让原版流程继续，不要吞掉普通攻击。</li>
 *   <li><b>只在客户端过滤输入，服务端仍要校验</b>：
 *       客户端只是不发默认攻击包，服务端不能因此认为「客户端已经校验过了」。</li>
 *   <li><b>目标选择用 {@code WeaponTargetingApi}</b>：
 *       <ul>
 *         <li>视觉 / 准心目标：{@code getVisibleAlivePlayerTarget(...)}、
 *             {@code resolveVisibleGunTarget(...)}</li>
 *         <li>真实命中目标：{@code getAttackableAlivePlayerTarget(...)}、
 *             {@code resolveAttackableGunTarget(...)}</li>
 *       </ul>
 *       <b>不要</b>自己拼 {@code ProjectileUtil} + 存活判断 + 可见性判断，
 *       也不要自己写一套目标选择 helper。</li>
 * </ol>
 *
 * <h2>最容易被忽略的语义差异</h2>
 * <p>{@code TARGET}（视觉）与 {@code ATTACK}（真实攻击）是两套语义。
 * 隐藏视觉目标不会让目标获得攻击免疫，
 * 反过来也不能因为准心看得见就认为一定能打到。</p>
 */
public final class FangClientPreAttackHandlers {
    private FangClientPreAttackHandlers() {
    }

    /**
     * 注册所有左键预攻击接管。
     *
     * <p>目前没有需要接管的武器，空实现。</p>
     */
    public static void register() {
        // 目前没有预攻击接管需求，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端预攻击接管注册完成（当前为空）");
    }
}
