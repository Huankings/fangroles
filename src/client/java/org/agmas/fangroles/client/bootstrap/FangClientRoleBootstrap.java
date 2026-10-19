package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端「职业」职责的聚合初始化入口。
 *
 * <p>本类负责把各职业自己的客户端初始化串起来，典型内容包括：</p>
 * <ul>
 *   <li>职业专属 Mood HUD 样式：{@code MoodHudApi.registerRoleStyle(role, style)}</li>
 *   <li>职业专属时间 HUD：{@code TimeHudApi.registerProvider(...)}</li>
 *   <li>职业专属雾效 provider（{@code FogOverrideApi}）</li>
 *   <li>职业客户端状态机的初始化</li>
 * </ul>
 *
 * <h2>不要在这里做这些事</h2>
 * <ul>
 *   <li>具体职业判定、目标解析、状态机逻辑 —— 放
 *       {@code client/roles/<role_id>/} 里自己的类。</li>
 *   <li>HUD provider 注册 —— 走 {@code hud/FangHudHandlers}。</li>
 *   <li>准心图标注册 —— 走 {@code crosshair/FangCrosshairHandlers}。</li>
 * </ul>
 *
 * <h2>雾效 provider 写法</h2>
 * <pre>{@code
 * FogOverrideApi.registerProvider(FangRolesCore.id("vanisher_fog"), 100, context -> {
 *     // 只给本人且处于特定状态时改雾距，其余情况必须返回 pass() 让低优先级继续
 *     if (!shouldOverride(context)) {
 *         return FogOverride.pass();
 *     }
 *     // 过渡时间属于玩法数值，要放进该职业的 *Constants，不要散落 magic number
 *     return FogOverride.of(start, end, shape);
 * });
 * }</pre>
 * <p><b>不要</b>直接注入 {@code WorldRenderer.render} 覆盖雾距，
 * 也不要写 Iris 私有类依赖 —— Wathe 的 {@code RenderSystemFogStateMixin}
 * 已经把最终雾距暴露给 RenderSystem getter。</p>
 */
public final class FangClientRoleBootstrap {
    private FangClientRoleBootstrap() {
    }

    /**
     * 注册所有职业的客户端初始化。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code client/roles/<role_id>/<RoleName>ClientBootstrap.register()}。</p>
     */
    public static void register() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端职业初始化完成（当前为空）");
    }
}
