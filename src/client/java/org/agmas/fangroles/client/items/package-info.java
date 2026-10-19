/**
 * fangroles 的物品客户端实现区。
 *
 * <p>注册编排在 {@code client/bootstrap/FangClientItemBootstrap}；本包放实现。</p>
 *
 * <h2>典型内容</h2>
 * <ul>
 *   <li>物品颜色 provider（按 NBT / 组件变色）</li>
 *   <li>Tooltip 动态附加文本（{@code ItemTooltipApi.registerAppender(...)}）</li>
 *   <li>模型谓词（例如「已装填 / 未装填」两套模型）</li>
 *   <li>物品专属输入处理（蓄力、直投 / 蓄力模式切换等）</li>
 * </ul>
 *
 * <h2>冷却读秒的硬性要求</h2>
 * <p>必须读取当前 {@code ItemCooldownManager.Entry} 的 {@code endTick - tick}。
 * <b>禁止</b>用 {@code GameConstants.ITEM_COOLDOWNS * getCooldownProgress(...)} 反推 ——
 * Wathe 的 {@code GunShotApi} modifier、开局冷却和多来源冷却会让实际值与常量不一致。</p>
 *
 * <h2>tooltip 的边界</h2>
 * <p>tooltip 是客户端显示，<b>不得</b>作为服务端购买、攻击或能力合法性判断依据。</p>
 *
 * <h2>物品输入与预攻击的分工</h2>
 * <ul>
 *   <li>「使用物品」（右键 / 长按右键 / 松开发射）→ 本包。</li>
 *   <li>「左键预攻击接管」（左键不开枪而是开镜 / 切模式）→
 *       {@code client/input/FangClientPreAttackHandlers} 与
 *       {@code client/roles/<role_id>/<RoleName>PreAttackHandler}。</li>
 * </ul>
 */
package org.agmas.fangroles.client.items;
