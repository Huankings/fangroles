package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端每 tick 调度入口。
 *
 * <h2>核心原则：能不用 tick 就不用 tick</h2>
 * <p>客户端 tick 逻辑是本地 CPU 消耗，而且很容易被误用来做「每帧重新计算上游状态」。
 * 优先考虑：</p>
 * <ol>
 *   <li><b>事件驱动</b>：能用 Wathe / Harpy 的事件（职业分配、死亡、回合结束）就不要 tick。</li>
 *   <li><b>状态变化驱动</b>：状态由服务端同步组件带来变化时，在组件变化处更新本地缓存，
 *       而不是每 tick 重新读一遍并比对。</li>
 *   <li><b>降频</b>：确实需要周期刷新的（例如倒计时显示），用计数器降到每 5~20 tick 一次。</li>
 * </ol>
 *
 * <h2>本类的职责</h2>
 * <p>本类<b>只</b>调度各职业 / 词条自己的 tick provider，
 * 例如 {@code client/roles/<role_id>/<RoleName>ClientTickHandler.tick()}。
 * 不要把具体逻辑写在本类里。</p>
 *
 * <h2>必须处理的边界</h2>
 * <ul>
 *   <li>{@code MinecraftClient.getInstance().player == null}（主菜单 / 断线瞬间）</li>
 *   <li>{@code world == null}</li>
 *   <li>玩家不在对局中、或处于旁观 / 创造模式</li>
 *   <li>本局已结束（结算阶段不要再推进状态机）</li>
 * </ul>
 * <p>这些情况下必须早退，否则会出现空指针崩溃或「结算后特效还在跑」。</p>
 */
public final class FangClientTickBootstrap {
    private FangClientTickBootstrap() {
    }

    /**
     * 注册客户端 tick 调度。
     *
     * <p>目前为空实现。新增时建议用
     * {@code ClientTickEvents.END_CLIENT_TICK.register(...)}，
     * 并在回调首行做上面列出的边界早退判断。</p>
     */
    public static void register() {
        // 目前没有需要每 tick 推进的客户端状态，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端 tick 调度注册完成（当前为空）");
    }
}
