package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端连接生命周期清理入口。
 *
 * <h2>为什么必须有这个类</h2>
 * <p>纯客户端缓存（皮肤、伪装映射、随机列表、输入状态、页码）不会随服务端组件重置而清空。
 * 如果不处理 JOIN / DISCONNECT，会出现：</p>
 * <ul>
 *   <li>换服务器后还显示上一个服务器的伪装皮肤</li>
 *   <li>本地玩家原始皮肤缓存里留着上一个玩家的数据</li>
 *   <li>背包按钮页码还停在上一次的第三页</li>
 *   <li>能力键的「按下边沿」状态残留，导致进服就自动触发一次能力</li>
 * </ul>
 *
 * <h2>需要清理的内容清单</h2>
 * <ol>
 *   <li>{@code client/appearance/} 下的原始皮肤缓存、伪装映射、随机映射</li>
 *   <li>{@code client/input/} 下的按键边沿标记、二阶段选择状态</li>
 *   <li>{@code client/inventory/} 下的分页状态与分组 widget</li>
 *   <li>{@code client/roles/<role_id>/} 下各职业的客户端状态机</li>
 * </ol>
 *
 * <h2>建议注册的事件</h2>
 * <pre>{@code
 * ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> resetAll());
 * ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> resetAll());
 * }</pre>
 *
 * <h2>边界说明</h2>
 * <p>只清理<b>纯客户端</b>缓存。服务端的组件状态由 Wathe / Harpy 的
 * 回合结束与 {@code ResetPlayerEvent} 负责，客户端不要越权去改服务端状态。</p>
 */
public final class FangClientConnectionBootstrap {
    private FangClientConnectionBootstrap() {
    }

    /**
     * 注册 JOIN / DISCONNECT 清理。
     *
     * <p>目前没有客户端缓存，空实现。
     * 一旦新增任何纯客户端缓存，必须回到本类补清理。</p>
     */
    public static void register() {
        // 目前没有客户端缓存，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端连接清理注册完成（当前为空）");
    }
}
