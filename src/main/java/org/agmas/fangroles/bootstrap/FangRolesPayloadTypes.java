package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 自定义网络包 payload codec 注册入口。
 *
 * <p>Wathe / NoellesRoles 的约定是<b>每个包一个类</b>，放在 {@code packet/} 下按用途分层：
 * <ul>
 *   <li>{@code packet/host/}：通用能力键之类的“宿主级”包（跨职业复用）</li>
 *   <li>{@code packet/item/}：物品相关的攻击 / 使用包</li>
 *   <li>{@code packet/role/<role_id>/}：某职业专属的包</li>
 *   <li>{@code packet/modifiers/<modifier_id>/}：某词条专属的包</li>
 * </ul>
 * 具体注册调用统一写在本类，不要在职业类里各自调用 {@code PayloadTypeRegistry}。</p>
 *
 * <h2>新增一个 C2S 包的标准写法</h2>
 * <pre>{@code
 * // packet/role/vanisher/VanisherC2SPacket.java
 * public record VanisherC2SPacket(int targetId) implements CustomPayload {
 *     public static final CustomPayload.Id<VanisherC2SPacket> ID =
 *             new CustomPayload.Id<>(FangRolesCore.id("vanisher_ability"));
 *     public static final PacketCodec<RegistryByteBuf, VanisherC2SPacket> CODEC =
 *             PacketCodec.of(VanisherC2SPacket::write, VanisherC2SPacket::read);
 *
 *     public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
 *     public void write(RegistryByteBuf buf) { buf.writeInt(targetId); }
 *     public static VanisherC2SPacket read(RegistryByteBuf buf) { return new VanisherC2SPacket(buf.readInt()); }
 * }
 *
 * // 然后在本类 register() 里加一行：
 * PayloadTypeRegistry.playC2S().register(VanisherC2SPacket.ID, VanisherC2SPacket.CODEC);
 * }</pre>
 *
 * <p><b>安全约定（必须遵守）</b>：C2S 包只表达「玩家意图」，
 * 服务端收到后必须重新校验职业、存活状态、冷却、距离、目标合法性和对局状态。
 * 绝对不能因为客户端发了包就直接执行效果。
 * 详见 {@link FangRolesPacketReceivers} 里的服务端校验说明。</p>
 */
public final class FangRolesPayloadTypes {
    private FangRolesPayloadTypes() {
    }

    /**
     * 注册所有 C2S / S2C payload codec。
     *
     * <p>调用时机：必须在任何发送 / 接收代码运行之前。
     * 本类由 {@link FangRolesBootstrap#init()} 压到最后调用，见那里的顺序说明。</p>
     */
    public static void register() {
        // 目前没有自定义网络包。
        // 新增时在此按「C2S 在前、S2C 在后」分组书写，并保留中文注释说明该包的用途。
        FangRolesCore.LOGGER.debug("[fangroles] payload types 注册完成（当前为空）");
    }
}
