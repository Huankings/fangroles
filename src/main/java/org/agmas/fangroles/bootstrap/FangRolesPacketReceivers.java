package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 服务端 packet receiver（接收器）注册入口。
 *
 * <p>与 {@link FangRolesPayloadTypes} 的分工：
 * <ul>
 *   <li>{@code FangRolesPayloadTypes} 只负责「让这个包在网络层合法」，即注册 codec；</li>
 *   <li>本类负责「收到包之后干什么」，即注册 {@code ServerPlayNetworking.registerGlobalReceiver(...)}。</li>
 * </ul>
 * 拆成两个类的原因是：codec 注册必须尽早（发送端也要用），
 * 而 receiver 属于服务端业务逻辑，可能依赖职业注册、组件、商店等已经就绪。</p>
 *
 * <h2>新增 receiver 的标准写法</h2>
 * <pre>{@code
 * ServerPlayNetworking.registerGlobalReceiver(VanisherC2SPacket.ID, (payload, context) -> {
 *     ServerPlayerEntity player = context.player();
 *     // 关键：网络回调不在主线程，所有世界 / 实体操作都要回到服务端线程执行，
 *     // 否则会出现并发修改世界的崩溃。
 *     context.server().execute(() -> VanisherAbilityHandler.handle(player, payload.targetId()));
 * });
 * }</pre>
 *
 * <h2>服务端必须重新校验的清单（不要相信客户端）</h2>
 * <ol>
 *   <li>玩家当前职业是否真的是该职业（从 {@code GameWorldComponent} 读，不要读客户端同步值反推）</li>
 *   <li>玩家是否存活：{@code GameFunctions.isPlayerAliveAndSurvival(...)}</li>
 *   <li>对局状态：是否正在进行中（开局前 / 结算后必须拒绝）</li>
 *   <li>冷却：按服务端记录的时间戳判断，不能信客户端倒计时</li>
 *   <li>距离与视线：目标是否在允许范围内、是否被 {@code TargetVisibilityApi.canAttackPlayer(...)} 允许</li>
 *   <li>特殊状态：被控制、被绑架、处于时间狭缝等情况下是否应该拒绝</li>
 * </ol>
 */
public final class FangRolesPacketReceivers {
    private FangRolesPacketReceivers() {
    }

    /**
     * 注册所有服务端 packet receiver。
     *
     * <p>由 {@link FangRolesBootstrap#init()} 在接近末尾处调用。</p>
     */
    public static void register() {
        // 目前没有自定义网络包，因此没有接收器。
        // 新增时在此为每个 payload 注册一个 receiver，并写清「本包表达了什么意图」。
        FangRolesCore.LOGGER.debug("[fangroles] 服务端 packet receiver 注册完成（当前为空）");
    }
}
