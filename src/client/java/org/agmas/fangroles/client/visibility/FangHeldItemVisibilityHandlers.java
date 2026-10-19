package org.agmas.fangroles.client.visibility;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 手持物隐藏（Held item invisibility）注册入口。
 *
 * <p>统一走 Wathe {@code HeldItemInvisibilityApi}，不要自己写渲染 mixin。</p>
 *
 * <h2>三个入口</h2>
 * <ul>
 *   <li>某职业某物品隐藏：{@code HeldItemInvisibilityApi.registerHiddenItem(role, item)}</li>
 *   <li>多物品：{@code registerHiddenItems(...)}</li>
 *   <li>动态状态隐藏：{@code registerRule(...)}
 *       —— 例如「只在被控制时隐藏」「只在拿起特定武器时隐藏」</li>
 * </ul>
 *
 * <h2>API 已经处理好的边界（不要重复实现）</h2>
 * <ol>
 *   <li><b>本人 F5 看自己仍然可见</b> —— 否则会让玩家以为自己没拿东西。</li>
 *   <li><b>死亡 / 旁观 / 创造模式可见</b> —— 旁观者需要看到真实物品状态。</li>
 *   <li><b>只隐藏局内存活玩家视角</b>。</li>
 * </ol>
 * <p>所以不要在本模组里再写一遍整套渲染 mixin 去处理这些情况，
 * 那会与 Wathe 的判断互相覆盖。</p>
 *
 * <h2>常见用途</h2>
 * <ul>
 *   <li>职业自带武器不该被其他人看到（例如隐形系职业的匕首）。</li>
 *   <li>特定状态下隐藏主手物品（例如伪装成平民）。</li>
 *   <li>与低心情幻觉冲突时，明确谁的优先级更高（幻觉是「脑补出物品」，
 *       隐藏是「看不到真实物品」，两者可以同时存在）。</li>
 * </ul>
 *
 * <h2>注意</h2>
 * <p>隐藏手持物只是视觉。服务端的攻击判定、伤害、命中音效
 * 都要按「物品真实存在」处理。</p>
 */
public final class FangHeldItemVisibilityHandlers {
    private FangHeldItemVisibilityHandlers() {
    }

    /**
     * 注册所有手持物隐藏规则。
     *
     * <p>目前没有职业，空实现。</p>
     */
    public static void register() {
        // 目前没有手持物隐藏需求，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 手持物隐藏注册完成（当前为空）");
    }
}
