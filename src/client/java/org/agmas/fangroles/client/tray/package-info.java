/**
 * fangroles 的托盘粒子 provider 实现区。
 *
 * <p>注册编排在 {@code client/bootstrap/FangClientParticleBootstrap}。</p>
 *
 * <h2>关键点：托盘粒子必须在客户端注册</h2>
 * <p>使用 Wathe {@code TrayParticleRegistry.registerProvider(...)} 注册客户端托盘粒子 provider。
 * 原生毒药粒子由 Wathe 默认 provider 调度，
 * 所以扩展<b>不要</b> mixin {@code BeveragePlateBlockEntity}。</p>
 *
 * <h2>托盘效果本身在哪</h2>
 * <ul>
 *   <li>效果逻辑（下毒、增益、标记）→ 服务端
 *       {@code tray/FangRolesTrayEffects}。</li>
 *   <li>取物上限规则 → 服务端
 *       {@code TrayTakeRegistry.registerRule(...)}，
 *       返回背包与主手同类物品的最大总数，Wathe 默认值为 1。</li>
 *   <li>粒子表现 → 本包。</li>
 * </ul>
 *
 * <h2>回放显示</h2>
 * <p>托盘的 placement / take / consume 回放由 Wathe 通用回放统一渲染成
 * 醒目的白色 {@code [效果名]}，效果名来自 {@code TrayEffectHandler#replayDisplay}。
 * 所以扩展<b>不需要</b>为每个托盘效果注册三套 formatter，
 * 只要实现 {@code replayDisplay} 并补好翻译即可。</p>
 */
package org.agmas.fangroles.client.tray;
