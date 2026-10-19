package org.agmas.fangroles.reset;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 玩家级与回合级重置规则的聚合入口。
 *
 * <h2>三个时机不能混用</h2>
 * <ul>
 *   <li><b>玩家级硬重置</b>：Harpy {@code ResetPlayerEvent}。用于清理开局前 / 结束时会留在
 *       玩家身上的组件字段、属性修饰符、药水效果和静态玩家缓存，覆盖掉线重连。</li>
 *   <li><b>新局分配前</b>：Wathe {@code GameEvents.ON_BEGIN_INITIALIZE}。用于清理世界组件里
 *       上一局的配对、倒计时、标记等数据，必须早于 Wathe 清背包、分配职业和扩展模式初始化；
 *       放到 {@code ON_FINISH_INITIALIZE} 已经太晚，旧状态会参与本局分配和存活判定。</li>
 *   <li><b>结算收尾</b>：Wathe {@code GameEvents.ON_FINISH_FINALIZE}。用于回收只在结算画面
 *       结束后才能安全清理的临时实体、播放管理器或世界级缓存。</li>
 * </ul>
 *
 * <h2>本类只做编排</h2>
 * <p>具体清理逻辑放 {@code roles/<role_id>/<RoleName>ResetHandler} 或
 * {@code modifiers/<modifier_id>/<ModifierName>ResetHandler}，本类只调用它们的 {@code init()}。
 * <b>不要</b>重新注入 {@code GameFunctions.resetPlayer} / {@code initializeGame} /
 * {@code finalizeGame}，也不要恢复旧式的 {@code *ReseterMixin} / 全局 reset 大块。</p>
 *
 * <p>重置必须成套：组件字段、静态 Map、延迟任务、临时物品和客户端缓存要一起清理；
 * 只清一半会在下一局留下“上局标记还在”“临时物品没回收”“冷却没重置”等问题。</p>
 */
public final class FangRolesResetBootstrap {
    private static boolean initialized;

    private FangRolesResetBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的重置规则。
     *
     * <p>新增职业时在这里调用对应所有者的 {@code *ResetHandler.init()}，
     * 由 handler 自己选择 {@code ResetPlayerEvent} / {@code ON_BEGIN_INITIALIZE} /
     * {@code ON_FINISH_FINALIZE} 中最合适的时机。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // 目前没有职业或词条需要清理状态，保留空入口。
        // 后续在这里调用各所有者的 *ResetHandler.init()。
        FangRolesCore.LOGGER.debug("[fangroles] 玩家 / 回合重置 API 接入完成（当前为空）");
    }
}
