package org.agmas.fangroles.client.inventory;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 背包按钮的共享辅助工具。
 *
 * <p>把「在线玩家列表」「玩家头像组件」「分页状态」「Wathe API 接入样板」
 * 这些每个职业选人按钮都要用的东西收敛在这里，
 * 避免每个职业的 {@code *InventoryButtons} 各写一遍。</p>
 *
 * <h2>典型用途</h2>
 * <ul>
 *   <li>取当前对局中可作为目标的玩家列表（过滤掉自己、死者、旁观者）。</li>
 *   <li>生成玩家头像 widget（Wathe 提供统一的头像组件，
 *       不要自己去拉皮肤贴图）。</li>
 *   <li>维护 {@code InventoryPageState} 的翻页与重置。</li>
 * </ul>
 *
 * <h2>必须过滤掉的目标</h2>
 * <ol>
 *   <li>本地玩家自己（除非职业设计允许对自己使用）</li>
 *   <li>已经死亡的玩家（用 Wathe 的存活定义，不要用 {@code isAlive()}）</li>
 *   <li>旁观 / 创造模式玩家</li>
 *   <li>处于特殊存活状态（例如时间狭缝）的玩家 —— 他们玩法上已经死亡</li>
 * </ol>
 * <p>第 4 条容易被忽略：时间狭缝会给旁观玩家加 alive override，
 * 使 {@code GameFunctions.isPlayerAliveAndSurvival(...)} 返回 true。
 * 如果本模组与 NoellesRoles 的时停者机制共处同一环境，
 * 选人列表必须显式排除这类玩家，否则会出现「能给死人上标记」的 bug。</p>
 *
 * <h2>注意</h2>
 * <p>本类只提供辅助方法，<b>不</b>保存跨 tick 状态。
 * 需要缓存的东西放对应职业的客户端状态类里。</p>
 */
public final class FangInventoryButtonSupport {
    private FangInventoryButtonSupport() {
    }

    /**
     * 调试日志统一出口。
     *
     * <p>背包按钮在打开界面时会被反复查询，日志量可能很大，
     * 不要在 provider 里无条件打印。</p>
     */
    public static void debug(String message) {
        FangRolesCore.LOGGER.debug("[fangroles][inventory] {}", message);
    }

    // 后续可继续在此补充：
    //   - 可选目标玩家列表（含上面 4 条过滤）
    //   - 头像 widget 工厂
    //   - 分页状态创建与重置
    //   - 「本地玩家是某职业」的统一判断（与 FangHudSupport 保持同一语义）
}
