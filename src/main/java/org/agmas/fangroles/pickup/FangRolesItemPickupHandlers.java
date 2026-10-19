package org.agmas.fangroles.pickup;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 普通掉落物拾取 API 的聚合注册器。
 *
 * <h2>服务端才是权威</h2>
 * <p>{@code ItemPickupApi} 在服务端判定普通 {@code ItemEntity} 是否允许进入原版拾取流程。
 * 扩展规则返回 {@code PASS}、{@code ALLOW} 或 {@code DENY}：</p>
 * <ul>
 *   <li>{@code PASS}：继续询问低优先级规则，全部 PASS 时回退 Wathe 默认枪械拾取规则。</li>
 *   <li>{@code DENY}：取消本次拾取，不调用原版碰撞拾取。</li>
 *   <li>{@code ALLOW}：只跳过 Wathe 的职业 / 枪械限制，仍然继续原版方法；
 *       不会跳过错开延迟、owner、背包空间、统计、进度准则和 Wathe 拾取回放。</li>
 * </ul>
 *
 * <h2>优先级语义</h2>
 * <p>priority 越大越先执行；同 priority 下后注册者先执行。强制缴械、重伤、招募等
 * {@code DENY} 规则必须使用高于普通拾取特权 {@code ALLOW} 的优先级，否则会被低优先级放行。</p>
 *
 * <p>本类只调用各职业 / 词条的 {@code *ItemPickupHandler.init()}。具体判定留在对应包内，
 * 不要重新 mixin {@code ItemEntity#onPlayerCollision} 复制原版拾取代码。
 * 该 API 也不接管箭、三叉戟和扩展自定义投射物的 {@code tryPickup}。</p>
 */
public final class FangRolesItemPickupHandlers {
    private static boolean initialized;

    private FangRolesItemPickupHandlers() {
    }

    /**
     * 注册所有职业 / 词条的掉落物拾取规则。
     *
     * <p>建议先列出强制禁拾规则，再列出只覆盖 Wathe 默认枪械限制的放行规则；
     * 真正的执行顺序由 {@code ItemPickupApi} 的 priority 决定，这里的顺序只用于源码可读性。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // 目前没有职业或词条需要限制拾取，保留空入口。
        // 后续在这里调用各所有者的 *ItemPickupHandler.init()。
        FangRolesCore.LOGGER.debug("[fangroles] 掉落物拾取 API 接入完成（当前为空）");
    }
}
