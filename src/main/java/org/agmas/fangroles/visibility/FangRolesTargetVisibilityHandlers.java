package org.agmas.fangroles.visibility;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 目标可见性 API 的总入口。
 *
 * <h2>这个 API 管什么</h2>
 * <p>{@code TargetVisibilityApi} 面向的问题是
 * 「某个观察者<b>能不能看见 / 选中 / 交互 / 攻击</b>某个玩家或玩家尸体」。
 * 它会同时接入 Wathe 的玩家与尸体渲染、{@code LivingEntity#canHit} 客户端选中、
 * RoleNameHud 尸体射线、默认刀枪准心目标、尸袋、匕首 / 枪击服务端命中和本能描边。</p>
 *
 * <h2>常用注册入口</h2>
 * <ul>
 *   <li>{@code TargetVisibilityApi.registerPlayerRule(...)}：玩家可见性规则</li>
 *   <li>{@code TargetVisibilityApi.registerBodyRule(...)}：尸体可见性规则</li>
 * </ul>
 *
 * <h2>不要写 mixin</h2>
 * <p>扩展隐藏尸体或隐藏玩家时优先注册 API 规则，
 * <b>不要</b> mixin {@code PlayerBodyEntityRenderer}、{@code LivingEntity#canHit}、
 * {@code RoleNameRenderer} 或 {@code CrosshairRenderer}。</p>
 *
 * <h2>最重要的一条：可见性不是权限</h2>
 * <p>API 只影响「客户端能不能看见 / 选中」。
 * 服务端职业能力和物品<b>仍然必须</b>在自己的 handler 里重新调用
 * {@code canInteractWithBody(...)}、{@code canInteractWithPlayer(...)}、
 * {@code canAttackPlayer(...)} / {@code canAttackEntity(...)} 做校验。
 * 否则玩家可以通过改客户端或伪造发包来攻击本该看不见的目标。</p>
 */
public final class FangRolesTargetVisibilityHandlers {
    private FangRolesTargetVisibilityHandlers() {
    }

    /**
     * 注册所有玩家 / 尸体的可见性规则。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>VisibilityHandler.init()}，
     * 或词条的 {@code modifiers/<modifier_id>/*VisibilityHandler.init()}。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 目标可见性 API 接入完成（当前为空）");
    }
}
