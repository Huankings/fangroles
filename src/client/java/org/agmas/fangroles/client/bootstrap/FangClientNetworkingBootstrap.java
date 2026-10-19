package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端 S2C 网络包接收器注册入口。
 *
 * <p>与 {@code bootstrap/FangRolesPayloadTypes}（注册 codec）和
 * {@code bootstrap/FangRolesPacketReceivers}（注册服务端 C2S 接收器）配套。
 * 三个类分别解决：包在网络层是否合法、服务端收到后做什么、客户端收到后做什么。</p>
 *
 * <h2>写法</h2>
 * <pre>{@code
 * ClientPlayNetworking.registerGlobalReceiver(SomeS2CPacket.ID, (payload, context) -> {
 *     // 客户端网络回调不一定在主线程，改动渲染 / 世界状态要回到客户端线程
 *     context.client().execute(() -> {
 *         SomeClientState.update(payload);
 *     });
 * });
 * }</pre>
 *
 * <h2>硬性边界</h2>
 * <ul>
 *   <li><b>S2C 只同步渲染所需状态</b>，客户端不得据此决定玩法结果
 *       （例如「我是否命中了目标」必须由服务端裁定并回传结果，不能客户端自己算）。</li>
 *   <li>收到的数据只更新<b>纯客户端缓存</b>，不要写回 CCA 组件
 *       （组件的权威值来自服务端同步）。</li>
 *   <li>纯客户端缓存必须能从「服务端重新同步」中恢复；
 *       断了 / 重连后不能依赖旧缓存。</li>
 * </ul>
 */
public final class FangClientNetworkingBootstrap {
    private FangClientNetworkingBootstrap() {
    }

    /**
     * 注册所有客户端 S2C receiver。
     *
     * <p>目前没有自定义网络包，空实现。</p>
     */
    public static void register() {
        // 目前没有 S2C 包，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端网络接收器注册完成（当前为空）");
    }
}
