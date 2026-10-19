/**
 * fangroles 的 Mixin 实现区。
 *
 * <h2>最高优先级原则：能用公开 API 就不要写 Mixin</h2>
 * <p>Wathe 已经把绝大多数扩展需要的能力收敛成公开 API。写 Mixin 之前，
 * <b>务必</b>按 {@code AGENTS.md} 的「Wathe 公开 API 优先表」逐项核对一遍。
 * 下面这些是老扩展里常见的 Mixin，现在都有对应 API，<b>不要</b>再写：</p>
 *
 * <table border="1">
 *   <caption>已被 API 取代的 Mixin</caption>
 *   <tr><th>不要写</th><th>改用</th></tr>
 *   <tr><td>{@code GunShootPayload} / {@code RevolverItem} / {@code DerringerItem}</td>
 *       <td>{@code GunShotApi}</td></tr>
 *   <tr><td>{@code GameFunctions.killPlayer}</td><td>{@code DeathApi}</td></tr>
 *   <tr><td>{@code InGameHud} / {@code RoleNameRenderer}</td>
 *       <td>{@code HudOverlayApi} / {@code RoleNameHudApi}</td></tr>
 *   <tr><td>{@code CrosshairRenderer}</td><td>{@code CrosshairHudApi}</td></tr>
 *   <tr><td>{@code PlayerBodyEntityRenderer} / {@code LivingEntity#canHit}</td>
 *       <td>{@code TargetVisibilityApi}</td></tr>
 *   <tr><td>{@code WorldBlackoutComponent} ticks</td><td>{@code BlackoutApi}</td></tr>
 *   <tr><td>{@code PlayerPsychoComponent} / {@code getArmour()}</td>
 *       <td>{@code PsychoModeApi}</td></tr>
 *   <tr><td>{@code Entity#collidesWith} / {@code EntityView#getEntityCollisions} / 推挤方法</td>
 *       <td>{@code PlayerCollisionApi}</td></tr>
 *   <tr><td>{@code travel()} / {@code jump()} / {@code getMovementSpeed()} / shadow 速度字段</td>
 *       <td>{@code PlayerMovementApi} / {@code PlayerStaminaApi}</td></tr>
 *   <tr><td>{@code WorldRenderer.render} 雾效</td><td>{@code FogOverrideApi}</td></tr>
 *   <tr><td>{@code ModdedMurderGameMode#findAndAssignPlayers} / {@code assignModifiers}</td>
 *       <td>Harpy {@code RoleAssignmentApi} / {@code ModifierAssignmentApi}</td></tr>
 *   <tr><td>{@code LimitedInventoryScreen} / {@code InventoryScreen}</td>
 *       <td>{@code InventoryButtonApi}</td></tr>
 *   <tr><td>{@code PlayerMoodComponent#completeTask}</td>
 *       <td>{@code MoodTaskApi.registerCompletionRule(...)}</td></tr>
 *   <tr><td>{@code BeveragePlateBlockEntity}</td>
 *       <td>{@code TrayEffectRegistry} / {@code TrayParticleRegistry}</td></tr>
 * </table>
 *
 * <h2>确实需要写 Mixin 时的硬性要求</h2>
 * <ol>
 *   <li><b>条件尽量窄</b>：在注入方法内部第一行就判断环境（服务端 / 客户端）、
 *       对局状态、职业，能早退就早退。</li>
 *   <li><b>区分环境</b>：只影响客户端渲染 / 输入的 Mixin <b>必须</b>注册到
 *       {@code fangroles.client.mixins.json} 并标 {@code "client"}，
 *       否则服务端会因为引用客户端类而崩溃。</li>
 *   <li><b>用 {@code @Inject} / {@code @ModifyVariable} 优先</b>，
 *       {@code @Overwrite} 只在万不得已时使用，并且要开
 *       {@code overwrites.requireAnnotations}。</li>
 *   <li><b>不要改上游字段的语义</b>：只读或加副作用，不要改变原方法返回值含义。</li>
 *   <li><b>写中文注释</b>说明「为什么 API 无法表达这个需求」，
 *       方便后续上游补 API 后删除本 Mixin。</li>
 * </ol>
 *
 * <h2>注册位置</h2>
 * <ul>
 *   <li>公共 / 服务端 Mixin：{@code src/main/resources/fangroles.mixins.json} 的 {@code mixins} 数组</li>
 *   <li>客户端 Mixin：{@code src/client/resources/fangroles.client.mixins.json} 的 {@code client} 数组</li>
 * </ul>
 */
package org.agmas.fangroles.mixin;
