package org.agmas.fangroles;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的粒子类型注册入口。
 *
 * <h2>注册分两步，缺一不可</h2>
 * <ol>
 *   <li><b>服务端 / 公共侧</b>（本类）：注册 {@code ParticleType}
 *       与 {@code FabricParticleTypes} 工厂。</li>
 *   <li><b>客户端侧</b>：在 {@code client/bootstrap/FangClientParticleBootstrap}
 *       注册 {@code ParticleFactoryRegistry}，提供实际渲染工厂。
 *       <b>不注册</b>会导致粒子出现时客户端崩溃。</li>
 * </ol>
 *
 * <h2>资源文件</h2>
 * <ul>
 *   <li>{@code assets/fangroles/particles/<name>.json}：粒子定义</li>
 *   <li>{@code assets/fangroles/textures/particle/<name>.png}：粒子贴图</li>
 * </ul>
 *
 * <h2>性能约束（重要）</h2>
 * <p>服务端粒子要估算收件人数，避免在每 tick 对每个粒子采样点调用广播接口。
 * 能本地渲染的效果优先只发送开始 / 结束 / 参数包，
 * 暂时不能客户端化时也要设置粒子预算和刷新间隔。</p>
 */
public final class FangRolesParticles {
    private FangRolesParticles() {
    }

    /**
     * 注册所有粒子类型。
     *
     * <p>由 {@code FangRolesBootstrap.init()} 调用。</p>
     */
    public static void init() {
        // 目前没有自定义粒子，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 粒子注册完成（当前为空）");
    }
}
