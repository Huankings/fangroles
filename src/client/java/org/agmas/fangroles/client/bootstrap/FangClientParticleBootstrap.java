package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.client.bed.FangRolesBedParticles;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端粒子注册入口。
 *
 * <p>与 {@code FangRolesParticles}（注册 {@code ParticleType}）配套：
 * 服务端侧注册类型，客户端侧注册「怎么渲染」。
 * <b>只注册一边</b>会导致粒子出现时客户端崩溃。</p>
 *
 * <h2>需要注册的三类粒子</h2>
 * <ol>
 *   <li><b>普通粒子</b>：{@code ParticleFactoryRegistry.getInstance().register(type, factory)}</li>
 *   <li><b>床粒子</b>：Wathe {@code BedParticleRegistry.registerProvider(...)}，
 *       统一由 {@link FangRolesBedParticles#init()} 聚合注册。
 *       床效果的服务端裁定仍走 {@code bed/FangRolesBedEffects}，
 *       两者是「服务端效果 / 客户端表现」的分工，
 *       所以扩展<b>不要</b>再注入 {@code TrimmedBedBlockEntity.clientTick}。</li>
 *   <li><b>托盘粒子</b>：Wathe {@code TrayParticleRegistry.registerProvider(...)}。
 *       原生毒药粒子由 Wathe 默认 provider 调度，
 *       所以扩展<b>不要</b> mixin {@code BeveragePlateBlockEntity}。</li>
 * </ol>
 *
 * <h2>性能约束</h2>
 * <ul>
 *   <li>粒子贴图用 {@code assets/fangroles/textures/particle/<name>.png}，
 *       配合 {@code assets/fangroles/particles/<name>.json}。</li>
 *   <li>服务端发送粒子的频率要估算收件人数。
 *       能本地渲染的效果优先只发开始 / 结束 / 参数包，
 *       不要每 tick 对每个采样点广播。</li>
 *   <li>粒子数量要有上限，避免「大范围效果 + 20 人同屏」时掉帧。</li>
 *   <li>床粒子 provider 是被床方块实体<b>每 tick</b> 调用的，
 *       内部必须做稀疏刷新（Wathe 原生与 NoellesRoles 都使用约 15% 的概率）。</li>
 * </ul>
 */
public final class FangClientParticleBootstrap {
    private FangClientParticleBootstrap() {
    }

    /**
     * 注册所有客户端粒子工厂、床粒子与托盘粒子 provider。
     *
     * <p>三类客户端粒子注册集中在这里，避免「粒子相关注册散落在多个 bootstrap」：</p>
     * <ol>
     *   <li>自定义粒子工厂：{@code ParticleFactoryRegistry.getInstance().register(...)}</li>
     *   <li>床粒子：{@link FangRolesBedParticles#init()}（Wathe {@code BedParticleRegistry}）</li>
     *   <li>托盘粒子：Wathe {@code TrayParticleRegistry.registerProvider(...)}</li>
     * </ol>
     */
    public static void register() {
        // 1) 自定义粒子工厂：当前没有自定义 ParticleType，暂无。
        // 2) 床粒子：把各职业的床效果视觉表现注册到 Wathe 的公开床粒子注册表。
        FangRolesBedParticles.init();
        // 3) 托盘粒子：当前没有自定义托盘效果，暂无。

        FangRolesCore.LOGGER.debug("[fangroles] 客户端粒子注册完成（床粒子聚合入口已接入，具体 provider 当前为空）");
    }
}
