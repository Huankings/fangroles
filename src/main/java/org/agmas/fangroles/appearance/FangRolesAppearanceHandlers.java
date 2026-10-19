package org.agmas.fangroles.appearance;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 外观（皮肤 / 尸体外观）注册入口。
 *
 * <h2>服务端外观 UUID 分配</h2>
 * <p>需要让某职业生成尸体时使用特定外观，用
 * {@code BodyAppearanceApi.register(...)} 在<b>服务端</b>生成外观 UUID，
 * 再由客户端渲染读取。不要在客户端凭空决定服务端身份。</p>
 *
 * <h2>客户端皮肤覆盖</h2>
 * <p>客户端侧的外观注册放在
 * {@code client/appearance/FangAppearanceHandlers}：
 * <ul>
 *   <li>玩家皮肤：{@code PlayerAppearanceApi.registerPlayerSkin(...)}</li>
 *   <li>客户端尸体皮肤覆盖：{@code PlayerAppearanceApi.registerBodySkin(...)}</li>
 * </ul>
 * </p>
 *
 * <h2>优先级语义（必须理解）</h2>
 * <ul>
 *   <li>priority 越大越先执行。</li>
 *   <li>handler 返回 {@code null} / {@code PASS} 表示让低优先级继续处理。</li>
 *   <li>全局限时伪装（例如召集者）应<b>高于</b>普通主动变形。</li>
 *   <li>「只影响自己客户端看到的一切」的视觉覆盖（例如灵术师出窍）应走客户端 handler，
 *       并且<b>不要</b>改变服务端真实身份或尸体 owner。</li>
 *   <li>低优先级外观（例如双重人格）应给主动变形让路。</li>
 * </ul>
 *
 * <h2>防伪装套娃</h2>
 * <p>要拿「目标原本的皮肤」时必须调用
 * {@code PlayerAppearanceApi.resolveOriginalSkinTextures(...)}，
 * <b>不要</b>读取目标实体当前已经被覆盖过的皮肤再二次套用，
 * 否则会逐层叠加出现错乱。</p>
 */
public final class FangRolesAppearanceHandlers {
    private FangRolesAppearanceHandlers() {
    }

    /**
     * 注册服务端外观相关逻辑（主要是尸体外观 UUID 分配）。
     *
     * <p>新增职业时在这里注册，并写中文注释说明「为什么外观由服务端裁定」。</p>
     */
    public static void register() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 服务端外观注册完成（当前为空）");
    }
}
