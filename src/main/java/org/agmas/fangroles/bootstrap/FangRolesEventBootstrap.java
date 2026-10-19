package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的全局事件监听、server tick 与回合清理注册入口。
 *
 * <h2>本类负责的典型内容</h2>
 * <ul>
 *   <li><b>ServerTickEvents.END_SERVER_TICK</b>：需要按 tick 推进的职业状态机（尽量少用，
 *       优先用 tick 计数 + 变化时同步，避免每 tick 全量广播造成带宽问题）。</li>
 *   <li><b>ServerPlayConnectionEvents.JOIN / DISCONNECT</b>：玩家加入 / 断线时补发状态或清理临时缓存。</li>
 *   <li><b>回合结束清理</b>：Wathe {@code GameEvents.ON_FINISH_FINALIZE} 等时机，
 *       把本模组所有「局内运行态」清干净（静态 Map、延迟任务、临时物品、客户端标记）。</li>
 *   <li><b>人数动态上限</b>：根据在线人数动态调整 Harpy 职业上限。</li>
 * </ul>
 *
 * <h2>清理必须成套，不能只清一半</h2>
 * <p>职业状态通常同时存在于多个地方，回合结束时必须全部处理，否则下一局会出现
 * 「上局的标记还在」「临时物品没回收」「冷却没重置」这类问题：</p>
 * <ol>
 *   <li>CCA 组件（玩家 / 世界 / scoreboard）的字段</li>
 *   <li>本模组自己的静态 {@code Map} / 缓存</li>
 *   <li>延迟任务队列（{@code server.execute(...)} 里排的后续 tick 逻辑）</li>
 *   <li>临时发放的物品（要带来源标记，只回收带标记的副本，不要按物品类型宽泛删除）</li>
 *   <li>客户端临时显示状态（由客户端 bootstrap 的 JOIN / DISCONNECT 入口处理）</li>
 * </ol>
 *
 * <h2>上游事件入口参考</h2>
 * <ul>
 *   <li>Harpy：{@code ModdedRoleAssigned}、{@code ModdedRoleRemoved}、
 *       {@code ModifierAssigned}、{@code ModifierRemoved}、{@code ResetPlayerEvent}</li>
 *   <li>Wathe：{@code GameEvents}（开局 / 结算 / 结束各阶段）、{@code GameFunctions}</li>
 * </ul>
 * 注意：加 Attribute 或其它会留在玩家身上的持久状态时，要用 {@code ResetPlayerEvent}
 * 而不是 {@code ModdedRoleRemoved}，因为前者在开局前和结束各触发一次，能覆盖掉线重连的情况。
 *
 * <h2>新局分配前清理（ON_BEGIN_INITIALIZE）</h2>
 * <p>世界组件里上一局残留的配对、倒计时、标记等状态，必须在 Wathe 清背包、分配职业和
 * 扩展模式初始化<b>之前</b>清理，否则会参与本局分配与存活判定。
 * 这个时机使用 {@code GameEvents.ON_BEGIN_INITIALIZE}，不要后移到
 * {@code ON_FINISH_INITIALIZE}，也不要再注入 {@code GameFunctions.initializeGame}。
 * 具体清理逻辑放各职业 / 词条的 {@code *ResetHandler}，由
 * {@link org.agmas.fangroles.reset.FangRolesResetBootstrap} 统一编排；本类不再集中写职业清理。</p>
 */
public final class FangRolesEventBootstrap {
    private FangRolesEventBootstrap() {
    }

    /**
     * 挂载所有全局事件。
     *
     * <p>由 {@link FangRolesBootstrap#init()} 在最后阶段调用，
     * 保证被清理的状态都已经注册完毕。</p>
     */
    public static void init() {
        // 目前没有需要监听的事件。
        // 新增时请按上面「清理必须成套」的清单实现对应清理，并写清为什么需要这个时机。
        // 玩家 / 世界重置不要写回本类，放 FangRolesResetBootstrap 聚合的 *ResetHandler。
        FangRolesCore.LOGGER.debug("[fangroles] 事件与回合清理注册完成（当前为空）");
    }
}
