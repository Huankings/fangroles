package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端「疯魔模式」注册入口。
 *
 * <p>与 {@code bootstrap/FangRolesPsychoBootstrap}（服务端 profile / 护盾规则）配套。
 * 本类只处理客户端表现：皮肤、背景音乐、HUD 样式。</p>
 *
 * <h2>皮肤：profile 默认值 + 客户端按优先级覆盖</h2>
 * <p>Wathe 的疯魔皮肤是双层机制：</p>
 * <ol>
 *   <li><b>profile 默认皮肤</b>：在服务端 profile 的
 *       {@code PsychoVisualSettings.skin(...)} 里给默认值。</li>
 *   <li><b>客户端覆盖</b>：需要按临时状态（例如「护盾破碎」「第二阶段」）改皮肤时，
 *       在本类用 {@code PsychoModeClientApi.registerVisualProvider(id, priority, provider)} 注册。
 *       priority 越大越先执行，返回 {@code PASS} 表示继续交给低优先级。</li>
 * </ol>
 *
 * <h2>背景音乐</h2>
 * <pre>{@code
 * PsychoModeClientApi.registerBackgroundAmbience(FangRolesSounds.SOME_PSYCHO_BGM, 20 * 60);
 * }</pre>
 * <p>{@code intervalTicks} 是循环间隔。只有确实需要客户端拼接播放的音乐才要注册，
 * 服务端 profile 的 {@code backgroundSound(sound, play)} 已经能覆盖大多数情况。</p>
 *
 * <h2>Mood HUD 样式</h2>
 * <p>自定义完整 / 破损图标、跑马文本、文本颜色和倒计时条颜色用
 * {@code MoodHudApi.registerPsychoStyle(...)} 返回 {@code PsychoMoodHudStyle}。
 * Wathe 会按当前护盾是否大于 0 切换 {@code body} / {@code hitBody}，
 * <b>0 护盾 profile 从启动开始就应显示破损图标</b>。
 * 颜色 provider 可返回 {@code 0xRRGGBB} 或 {@code 0xAARRGGBB}，
 * 渲染器会重新写入 HUD 需要的 alpha。</p>
 */
public final class FangClientPsychoBootstrap {
    private FangClientPsychoBootstrap() {
    }

    /**
     * 注册所有疯魔客户端表现。
     *
     * <p>目前没有职业疯魔形态，空实现。</p>
     */
    public static void register() {
        // 目前没有疯魔形态，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端疯魔注册完成（当前为空）");
    }
}
