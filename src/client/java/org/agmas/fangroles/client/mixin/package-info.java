/**
 * fangroles 的客户端 Mixin 实现区。
 *
 * <h2>注册位置</h2>
 * <p>本包的 Mixin <b>必须</b>注册到
 * {@code src/client/resources/fangroles.client.mixins.json} 的 {@code client} 数组。
 * 如果误注册到 {@code fangroles.mixins.json}，服务端会因为引用客户端类而启动失败。</p>
 *
 * <h2>优先级原则同主 Mixin 包</h2>
 * <p>客户端也有大量公开 API 可用，写 Mixin 前先核对
 * {@code src/main/java/org/agmas/fangroles/mixin/package-info.java} 里的对照表。
 * 客户端专属的替代关系补充如下：</p>
 *
 * <table border="1">
 *   <caption>客户端侧已被 API 取代的 Mixin</caption>
 *   <tr><th>不要写</th><th>改用</th></tr>
 *   <tr><td>{@code InGameHud}</td><td>{@code HudOverlayApi}</td></tr>
 *   <tr><td>{@code CrosshairRenderer}</td><td>{@code CrosshairHudApi}</td></tr>
 *   <tr><td>{@code RoleNameRenderer}</td><td>{@code RoleNameHudApi}</td></tr>
 *   <tr><td>{@code PlayerEntityRenderer}（皮肤伪装）</td><td>{@code PlayerAppearanceApi}</td></tr>
 *   <tr><td>{@code WorldRenderer.render}（雾效）</td><td>{@code FogOverrideApi}</td></tr>
 *   <tr><td>{@code InventoryScreen} / {@code LimitedInventoryScreen}</td>
 *       <td>{@code InventoryButtonApi}</td></tr>
 *   <tr><td>{@code HeldItemRenderer}（手持物隐藏）</td>
 *       <td>{@code HeldItemInvisibilityApi}</td></tr>
 * </table>
 *
 * <h2>确实需要写客户端 Mixin 时的要求</h2>
 * <ol>
 *   <li>注入点尽量窄，首行就判断环境与状态，能早退就早退。</li>
 *   <li>不要改变原方法返回值语义；只读或加副作用。</li>
 *   <li>写中文注释说明「为什么 API 无法表达这个需求」，
 *       便于后续上游补 API 后删除本 Mixin。</li>
 *   <li>按职业拆到 {@code client/mixin/roles/<role_id>/}，
 *       按词条拆到 {@code client/mixin/modifiers/<modifier_id>/}，
 *       不要把所有客户端 Mixin 堆在根包下。</li>
 * </ol>
 */
package org.agmas.fangroles.client.mixin;
