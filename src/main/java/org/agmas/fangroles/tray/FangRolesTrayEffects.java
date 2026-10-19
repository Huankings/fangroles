package org.agmas.fangroles.tray;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 托盘效果（Tray effect）注册入口。
 *
 * <p>托盘是 Wathe 的饮料 / 毒药交互载体。扩展要给托盘加自定义效果时走
 * {@code TrayEffectRegistry}，<b>不要</b> mixin {@code BeveragePlateBlockEntity}。</p>
 *
 * <h2>接入要点</h2>
 * <ul>
 *   <li><b>效果实现</b>：实现 {@code TrayEffectHandler}，并实现
 *       {@code replayDisplay} 提供效果的本地化名称。
 *       Wathe 的通用回放会把效果渲染成醒目的白色 {@code [效果名]}，
 *       所以扩展<b>不需要</b>再为每个托盘效果注册 placement / take / consume
 *       三套 formatter。</li>
 *   <li><b>取物上限</b>：用 {@code TrayTakeRegistry.registerRule(...)} 注册取物上限规则，
 *       返回背包与主手同类物品的最大总数。Wathe 默认值为 1。</li>
 *   <li><b>粒子</b>：用 {@code TrayParticleRegistry.registerProvider(...)} 注册，
 *       而且要在<b>客户端</b>注册（见 {@code client/tray/}）。
 *       原生毒药粒子由 Wathe 默认 provider 调度。</li>
 * </ul>
 *
 * <h2>翻译 key</h2>
 * <p>效果名要补 {@code tray_effect.fangroles.<name>} 之类的翻译，
 * 否则回放里会显示原始 key。</p>
 */
public final class FangRolesTrayEffects {
    private FangRolesTrayEffects() {
    }

    /**
     * 注册所有托盘效果与取物规则。
     *
     * <p>新增效果时在这里注册，并写中文注释说明效果语义（下毒 / 增益 / 标记等）。</p>
     */
    public static void register() {
        // 目前没有托盘效果，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 托盘效果注册完成（当前为空）");
    }
}
