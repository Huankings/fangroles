package org.agmas.fangroles.client.inventory;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 背包按钮与分页的总注册入口。
 *
 * <p>统一走 Wathe {@code InventoryButtonApi}，
 * <b>不要</b>新增 {@code LimitedInventoryScreen} / {@code InventoryScreen} /
 * {@code CreativeInventoryScreen} 的 screen mixin。</p>
 *
 * <h2>三种 screen 类型</h2>
 * <ul>
 *   <li>{@code InventoryScreenType.LIMITED}：Wathe 局内限制背包。
 *       职业选人按钮通常只挂这里。</li>
 *   <li>{@code InventoryScreenType.VANILLA}：原版玩家背包。
 *       适合图鉴、帮助入口等非职业状态按钮。</li>
 *   <li>{@code InventoryScreenType.CREATIVE}：创造背包。
 *       适合创造模式也要显示的入口。</li>
 * </ul>
 *
 * <h2>运行时约定</h2>
 * <ul>
 *   <li>provider 返回 {@code null} 表示当前 screen 不需要按钮。</li>
 *   <li>{@code InventoryButtonContext.addWidget(groupId, widget)} 会把 widget
 *       加入<b>命名分组</b>。动态列表变化用 {@code replaceGroup} /
 *       {@code clearGroup} / {@code setGroupVisible}，
 *       <b>不要</b>直接操作 screen 内部 children。</li>
 *   <li>「删除 widget」在 API 里表现为<b>隐藏、禁用并解除焦点</b>，
 *       因为 MC 的 screen 内部列表没有稳定公开的删除接口。
 *       所以不要期待 widget 被真正移除。</li>
 *   <li>需要换页时使用 {@code InventoryButtonLayout}、{@code InventoryPageState}、
 *       {@code InventoryPageSwitchWidget}。Wathe 会在开局、停局、
 *       结算完成和断线时统一 reset 页码，扩展不要自己再写一套重置。</li>
 *   <li>输入框或二阶段选择期间需要禁止 E 键关背包时，
 *       在 {@code InventoryButtonExtension.allowInventoryKeyClose(...)} 返回 {@code false}，
 *       并在 {@code close(...)} 里清理静态输入状态。</li>
 *   <li>「点击后头像和翻页按钮都要消失」这类场景，
 *       要让头像和翻页按钮共享同一个可见性条件或同一个 group，
 *       不要只隐藏其中一部分。</li>
 * </ul>
 *
 * <h2>文件组织</h2>
 * <p>按职业拆到 {@code client/ui/roles/<role_id>/<RoleName>InventoryButtons.java}，
 * 按词条拆到 {@code client/ui/modifiers/<modifier_id>/}，
 * 本类只调用各自的 {@code register()}。</p>
 */
public final class FangInventoryButtons {
    private FangInventoryButtons() {
    }

    /**
     * 注册所有背包按钮 provider。
     *
     * <p>目前没有职业，空实现。</p>
     */
    public static void register() {
        // 目前没有背包按钮，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 背包按钮注册完成（当前为空）");
    }
}
