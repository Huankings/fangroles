/**
 * fangroles 的客户端粒子实现区。
 *
 * <p>注册编排在 {@code client/bootstrap/FangClientParticleBootstrap}；本包放粒子工厂实现。</p>
 *
 * <h2>三类粒子的归属</h2>
 * <ul>
 *   <li><b>普通自定义粒子</b>：粒子工厂实现放本包；{@code ParticleType} 注册在服务端侧
 *       {@code FangRolesParticles}（两侧都要注册，缺一边会崩）。</li>
 *   <li><b>床粒子</b>：provider 放 {@code client/roles/<role_id>/<RoleName>BedParticleHandler}，
 *       由 {@code client/bed/FangRolesBedParticles} 聚合注册。
 *       本包不负责床粒子，避免和「自定义粒子工厂」混淆。</li>
 *   <li><b>托盘粒子</b>：provider 放 {@code client/tray/}。</li>
 * </ul>
 *
 * <h2>必需资源文件</h2>
 * <pre>{@code
 * src/main/resources/assets/fangroles/particles/<name>.json   ← 粒子定义（两侧共用）
 * src/main/resources/assets/fangroles/textures/particle/<name>.png
 * }</pre>
 * <p>注意 {@code particles/<name>.json} 放在 {@code src/main/resources}，
 * 因为粒子定义的 json 由资源包加载，代码里的工厂注册才放客户端源码集。</p>
 *
 * <h2>性能纪律</h2>
 * <ul>
 *   <li>为粒子设置数量上限，避免大范围效果在 20 人同屏时掉帧。</li>
 *   <li>能客户端本地推导的效果，只发「开始 / 结束 / 参数」包，
 *       不要每 tick 对每个采样点广播。</li>
 *   <li>确需服务端持续发送时，设置粒子预算和刷新间隔。</li>
 * </ul>
 */
package org.agmas.fangroles.client.particle;
