/**
 * fangroles 自定义网络包实现区。
 *
 * <h2>分层约定</h2>
 * <pre>{@code
 * packet/host/                通用能力键等跨职业复用的「宿主级」包
 * packet/item/                物品相关的攻击 / 使用包
 * packet/role/<role_id>/      某职业专属的包
 * packet/modifiers/<modifier_id>/  某词条专属的包
 * }</pre>
 *
 * <h2>每个包的写法</h2>
 * <p>使用 record + {@code CustomPayload} + {@code PacketCodec}。
 * 完整范例见 {@code bootstrap/FangRolesPayloadTypes} 的 Javadoc。</p>
 *
 * <h2>C2S / S2C 的职责边界（硬性规则）</h2>
 * <ul>
 *   <li><b>C2S 只表达玩家意图</b>。服务端收到后必须重新校验职业、存活、
 *       冷却、距离、目标合法性和对局状态。</li>
 *   <li><b>S2C 只同步客户端渲染所需状态</b>。不要让客户端决定服务端玩法结果。</li>
 *   <li>新增包要同时补：payload record、codec 注册
 *       （{@code bootstrap/FangRolesPayloadTypes}）、
 *       接收器注册（{@code bootstrap/FangRolesPacketReceivers}，C2S）、
 *       客户端 receiver（{@code client/bootstrap/FangClientNetworkingBootstrap}，S2C）。</li>
 * </ul>
 *
 * <h2>带宽约束</h2>
 * <ul>
 *   <li>只传客户端必需字段。</li>
 *   <li>多个布尔值用 bit mask 打包。</li>
 *   <li>角度 / 坐标按需要量化。</li>
 *   <li>输入包要有变化检测或心跳间隔，
 *       <b>不要</b>无条件每 tick 重复发送完整状态。</li>
 * </ul>
 */
package org.agmas.fangroles.packet;
