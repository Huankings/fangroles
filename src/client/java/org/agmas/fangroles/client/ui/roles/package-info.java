/**
 * fangroles 的背包选人界面实现区。
 *
 * <h2>目录约定</h2>
 * <pre>{@code
 * client/ui/roles/<role_id>/<RoleName>InventoryButtons.java
 * client/ui/modifiers/<modifier_id>/<ModifierName>InventoryButtons.java
 * }</pre>
 * <p>注册编排在 {@code client/inventory/FangInventoryButtons}；
 * 共享工具在 {@code client/inventory/FangInventoryButtonSupport}。</p>
 *
 * <h2>Wathe API 速查</h2>
 * <ul>
 *   <li>注册 provider：{@code InventoryButtonApi.registerProvider(id, priority, provider)}</li>
 *   <li>screen 类型：{@code InventoryScreenType.LIMITED} / {@code VANILLA} / {@code CREATIVE}</li>
 *   <li>分组管理：{@code InventoryButtonContext.addWidget(groupId, widget)} /
 *       {@code replaceGroup} / {@code clearGroup} / {@code setGroupVisible}</li>
 *   <li>换页：{@code InventoryButtonLayout} / {@code InventoryPageState} /
 *       {@code InventoryPageSwitchWidget}</li>
 *   <li>禁止 E 键关背包：{@code InventoryButtonExtension.allowInventoryKeyClose(...)}</li>
 * </ul>
 *
 * <h2>必须处理好的交互细节</h2>
 * <ol>
 *   <li><b>页码重置</b>：Wathe 已统一在开局、停局、结算完成、断线时 reset 页码，
 *       扩展不要自己再写一套；否则会出现「Wathe 重置成第 1 页，
 *       扩展又设回第 3 页」的竞争。</li>
 *   <li><b>二阶段选择状态</b>：需要在 {@code close(...)} 里清理静态输入状态，
 *       并在 {@code allowInventoryKeyClose(...)} 返回 {@code false} 阻止误关。</li>
 *   <li><b>可见性联动</b>：头像与翻页按钮要共享同一可见性条件 / 同一 group，
 *       避免只隐藏其中一部分。</li>
 *   <li><b>目标列表过滤</b>：用 {@code FangInventoryButtonSupport} 的过滤规则，
 *       尤其要排除特殊存活状态（时间狭缝）的玩家。</li>
 * </ol>
 */
package org.agmas.fangroles.client.ui.roles;
