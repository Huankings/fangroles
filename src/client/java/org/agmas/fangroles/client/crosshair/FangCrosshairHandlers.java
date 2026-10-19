package org.agmas.fangroles.client.crosshair;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 准心 HUD 注册入口。
 *
 * <p>统一走 Wathe {@code CrosshairHudApi}，<b>不要</b> mixin {@code CrosshairRenderer}。</p>
 *
 * <h2>两个入口，用途不同</h2>
 * <ul>
 *   <li><b>替换屏幕中心 3x3 准心，或准心下方 10x7 小图标</b>：
 *       {@code CrosshairHudApi.registerProvider(...)}。
 *       <ul>
 *         <li>返回 {@code PASS}：继续交给后续 provider 或 Wathe 默认准心。</li>
 *         <li>返回 {@code HANDLED}：本帧已处理完，
 *             包含「狙击枪开镜故意隐藏默认准心」这种情况。</li>
 *       </ul>
 *   </li>
 *   <li><b>只在默认准心后额外补进度条</b>：
 *       {@code CrosshairHudApi.registerOverlay(...)}，
 *       例如时停者怀表的冷却 / 蓄力条。</li>
 * </ul>
 *
 * <h2>准心图标只是客户端提示</h2>
 * <p>服务端仍必须重新校验职业、存活、冷却、距离和目标合法性。
 * 不要因为「客户端准心已经变红了」就认为服务端可以放行。</p>
 *
 * <h2>文件组织</h2>
 * <p>按职业或词条拆文件，例如
 * {@code client/roles/<role_id>/<RoleName>CrosshairHandler.java}，
 * 本类只调用各自的 {@code register()}。</p>
 *
 * <h2>贴图资源</h2>
 * <p>HUD 图标放 {@code assets/fangroles/textures/gui/sprites/hud/<name>.png}，
 * 并在代码里用 {@code Identifier} 引用，
 * 不要硬编码绝对路径或从别的模组命名空间借用贴图。</p>
 */
public final class FangCrosshairHandlers {
    private FangCrosshairHandlers() {
    }

    /**
     * 注册所有准心 provider 与 overlay。
     *
     * <p>目前没有职业准心需求，空实现。</p>
     */
    public static void register() {
        // 目前没有准心需求，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 准心 HUD 注册完成（当前为空）");
    }
}
