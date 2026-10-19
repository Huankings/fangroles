package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 停电机制（Blackout）API 的总入口。
 *
 * <h2>绝对不要再做的两件事</h2>
 * <ol>
 *   <li><b>不要</b> mixin {@code WorldBlackoutComponent} 的私有 tick 字段。</li>
 *   <li><b>不要</b>在扩展客户端监听 {@code WatheSounds.AMBIENT_BLACKOUT} 自己计时黑幕。
 *       Wathe 已经用服务端同步的 {@code WorldBlackoutComponent} 统一驱动黑幕 HUD。</li>
 * </ol>
 *
 * <h2>正确入口</h2>
 * <ul>
 *   <li><b>触发停电</b>：服务端 {@code BlackoutApi.trigger(world)}</li>
 *   <li><b>恢复电力</b>：服务端 {@code BlackoutApi.restorePower(world)}
 *       —— 它会恢复灯光、清空停电倒计时、同步客户端黑幕，
 *       并清理 Wathe 自己发放的停电夜视 / 失明。不要自己拼这套流程。</li>
 *   <li><b>改停电时长</b>：{@code BlackoutApi.registerDurationModifier(id, priority, handler)}
 *       返回 {@code BlackoutDuration.of(minTicks, maxTicks)}</li>
 *   <li><b>改药水效果</b>：{@code BlackoutApi.registerEffectRule(id, priority, handler)}
 *       返回 {@code BlackoutEffectResult.nightVision()} / {@code blindness()} /
 *       {@code none()} / {@code pass()}</li>
 * </ul>
 *
 * <h2>Wathe 默认规则（职业侧只需要覆盖差异部分）</h2>
 * <ul>
 *   <li>杀手阵营获得夜视；平民、义警、中立获得失明。</li>
 *   <li>玩家只要拥有夜视，停电黑幕会完全消失；Wathe 自己发放的失明也会让位并解除。</li>
 * </ul>
 * <p>所以「杀手侧中立也应该有夜视」这类需求，用一条分组规则覆盖即可，
 * 不需要给每个职业单独写一条。</p>
 *
 * <h2>调试指令</h2>
 * <p>{@code /wathe:blackout trigger}、{@code /wathe:blackout restore}、
 * {@code /wathe:blackout overlay <0-100>}、{@code /wathe:blackout potionEffects <true|false>}</p>
 *
 * <h2>性能约束（不要改回旧实现）</h2>
 * <ul>
 *   <li>大地图灯光恢复由 {@code WorldBlackoutComponent} 按 {@code ChunkSection} 分组、
 *       通过预算队列调度。<b>不要</b>改回「每 tick 遍历所有灯逐灯倒计时」。</li>
 *   <li>每盏灯在自然恢复阶段只允许写入一次最终状态；
 *       恢复开始和完全恢复各只播放一次全局声音，<b>禁止</b>逐灯恢复声音包。</li>
 *   <li>恢复阶段的声音只能用 {@code BLACKOUT_RECOVERY_SOUND_MIN_INTERVAL} /
 *       {@code MAX_INTERVAL} 控制的全局随机间隔循环声，
 *       并且必须在灯全部恢复、强制恢复、停局和新局时清零。</li>
 *   <li>停电 CCA 的客户端同步只包含时间线、黑幕和药水配置，
 *       <b>不要</b>把完整灯坐标列表发给客户端。</li>
 * </ul>
 */
public final class FangRolesBlackoutBootstrap {
    private FangRolesBlackoutBootstrap() {
    }

    /**
     * 注册所有停电相关规则。
     *
     * <p>建议先放分组规则（例如「某组中立获得夜视」），
     * 再按职业调用 {@code roles/<role_id>/<RoleName>BlackoutHandler.init()}。</p>
     */
    public static void init() {
        // 目前没有职业与分组，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 停电 API 接入完成（当前为空）");
    }
}
