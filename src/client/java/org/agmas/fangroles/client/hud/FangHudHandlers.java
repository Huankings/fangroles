package org.agmas.fangroles.client.hud;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 通用屏幕 HUD 的总注册入口。
 *
 * <p>本类<b>只</b>调用各职业 / 词条自己的 {@code register()}，
 * 具体 HUD 实现放在
 * {@code client/roles/<role_id>/<RoleName>StatusHud.java} 与
 * {@code client/hud/modifiers/<modifier_id>/<ModifierName>Hud.java}。</p>
 *
 * <h2>三个注册入口，选错会导致 HUD 在不该出现时出现</h2>
 * <ul>
 *   <li><b>职业专属状态 HUD</b>（右下角）：{@code HudOverlayApi.registerAliveRole(...)}。
 *       它会同时判断本地玩家职业和 Wathe 的
 *       {@code GameFunctions.isPlayerAliveAndSurvival(...)} 存活定义。</li>
 *   <li><b>非职业专属但仍只给活人看</b>（被控制、被绑架、开局安全、停电提示、狙击镜遮罩）：
 *       {@code HudOverlayApi.register(...)}，
 *       并且必须在 provider 内显式检查 {@code context.aliveAndSurvival()}。</li>
 *   <li><b>旁观 / 创造也要看</b>：同样用 {@code HudOverlayApi.register(...)}，
 *       但不要检查存活，而是检查职业和旁观状态。</li>
 * </ul>
 *
 * <h2>图层与优先级</h2>
 * <ul>
 *   <li>{@code BEFORE_HUD}：尽早盖住画面的提示。</li>
 *   <li>{@code MAIN_HUD}：常规状态文字。</li>
 *   <li>{@code AFTER_HUD}：压在最上层的遮罩；需要复画快捷栏时调用
 *       {@code context.renderHotbar()}。</li>
 * </ul>
 * <p>同一 layer 内 priority 越大越<b>晚</b>渲染，因此会盖住低 priority 的 HUD。</p>
 *
 * <h2>不要新增的 Mixin</h2>
 * <p>普通 HUD <b>不要</b>再新增 {@code InGameHud} / {@code RoleNameRenderer} 的 mixin。
 * 如果某个旧 mixin 已被 API 覆盖，要同步删除源码文件和 mixin json 里的条目。</p>
 *
 * <h2>布局与存活过滤辅助</h2>
 * <p>右下角文字坐标、职业过滤、玩家名兜底统一用
 * {@link FangHudSupport}，不要在各自的 HUD 类里重复写一遍。</p>
 */
public final class FangHudHandlers {
    private FangHudHandlers() {
    }

    /**
     * 注册所有 HUD provider。
     *
     * <p>新增职业时在这里调用该职业 HUD 类的 {@code register()}。</p>
     */
    public static void register() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] HUD 注册完成（当前为空）");
    }
}
