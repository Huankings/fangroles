package org.agmas.fangroles.client.mood;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 低心情幻觉手持物（Psychosis）注册入口。
 *
 * <p>统一走 Wathe {@code dev.doctor4t.wathe.api.client.mood.PsychosisItemApi}。
 * <b>不要</b>直接读 {@code PlayerMoodComponent} 的幻觉 Map，
 * 也不要新增玩家渲染 mixin。</p>
 *
 * <h2>这个 API 管什么</h2>
 * <p>让观察者客户端「脑补」目标手持指定 / 随机物品，或指定手臂姿势。
 * 例如：低心情时看到别人手里拿着刀。</p>
 *
 * <h2>priority 语义</h2>
 * <ul>
 *   <li>Wathe 默认幻觉是 <b>priority 0</b>。</li>
 *   <li>更高 priority 会<b>覆盖</b>默认结果。</li>
 *   <li>更低或同等 priority 只在默认结果 {@code PASS} 时生效。</li>
 *   <li>返回 {@code Result.PASS} 表示继续链路。</li>
 * </ul>
 *
 * <h2>最重要的边界：这是纯客户端视觉</h2>
 * <p>本 API <b>只改变客户端看到的画面</b>，
 * <b>不</b>代表真实物品，也<b>不</b>参与服务端攻击判定。
 * 所以：</p>
 * <ul>
 *   <li>不要用幻觉手持物来决定服务端能不能打人。</li>
 *   <li>不要因为玩家「看起来拿着刀」就在服务端给他刀的伤害。</li>
 * </ul>
 *
 * <h2>缓存生命周期</h2>
 * <p>普通死亡、停局、断线会清除幻觉缓存。
 * 具有 {@code PlayerLifeStateApi} 特殊存活授权的 spectator / creative
 * 仍然保留并显示低心情幻觉 —— 这是 Wathe 的既有行为，扩展不要额外覆盖。</p>
 */
public final class FangPsychosisHandlers {
    private FangPsychosisHandlers() {
    }

    /**
     * 注册所有幻觉手持物 provider。
     *
     * <p>目前没有职业 / 词条需要注册，空实现。</p>
     */
    public static void register() {
        // 目前没有幻觉需求，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 低心情幻觉注册完成（当前为空）");
    }
}
