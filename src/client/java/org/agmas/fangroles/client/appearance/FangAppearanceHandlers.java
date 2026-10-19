package org.agmas.fangroles.client.appearance;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端外观（皮肤 / 伪装）注册与缓存入口。
 *
 * <h2>注册入口</h2>
 * <ul>
 *   <li>玩家皮肤：{@code PlayerAppearanceApi.registerPlayerSkin(...)}</li>
 *   <li>客户端尸体皮肤覆盖：{@code PlayerAppearanceApi.registerBodySkin(...)}</li>
 * </ul>
 *
 * <h2>priority 语义（与其它 Wathe API 一致）</h2>
 * <ul>
 *   <li>priority 越大越先执行。</li>
 *   <li>handler 返回 {@code null} / {@code PASS} 表示让低优先级继续处理。</li>
 *   <li>全局限时伪装（例如召集者）应<b>高于</b>普通主动变形。</li>
 *   <li>「只影响自己客户端看到的一切」的视觉覆盖（例如灵术师出窍）
 *       走客户端 handler，并且<b>不要</b>改变服务端真实身份 / 尸体 owner。</li>
 *   <li>低优先级外观（例如双重人格）应给主动变形让路。</li>
 * </ul>
 *
 * <h2>原始皮肤缓存（本包的核心职责之一）</h2>
 * <p>做「伪装成某个玩家」时，必须拿目标<b>原本的</b>皮肤，
 * 而不是目标当前已经被覆盖过的皮肤，否则会逐层套娃。
 * Wathe 提供 {@code PlayerAppearanceApi.resolveOriginalSkinTextures(...)}；
 * 如果本模组需要本地缓存原始皮肤，缓存放在本包，
 * 并且要在 JOIN / DISCONNECT 时清理
 * （见 {@code bootstrap/FangClientConnectionBootstrap}）。</p>
 *
 * <h2>纯客户端状态不要进时停快照</h2>
 * <p>外观缓存、随机伪装映射这类纯客户端显示状态，
 * 属于「本机渲染用」，<b>不</b>应注册到跨模组的时间回溯快照里。
 * 服务端权威身份由 CCA 组件承载，回溯时要恢复的是组件，不是本地缓存。</p>
 */
public final class FangAppearanceHandlers {
    private FangAppearanceHandlers() {
    }

    /**
     * 注册所有客户端外观规则。
     *
     * <p>目前没有职业，空实现。</p>
     */
    public static void register() {
        // 目前没有外观需求，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端外观注册完成（当前为空）");
    }
}
