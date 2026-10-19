/**
 * fangroles 的词条（Harpy Modifier）实现区。
 *
 * <h2>目录约定：一个词条一个子包</h2>
 * <pre>{@code
 * modifiers/<modifier_id>/
 *   <ModifierName>Constants.java                  数值常量
 *   <ModifierName>ModifierAssignmentRules.java    Harpy 词条分配规则
 *   <ModifierName>ModifierAssignedHandler.java    分配后初始化
 *   <ModifierName>PlayerComponent.java            CCA 玩家组件
 *   <ModifierName>EffectHandler.java              具体效果实现
 *   <ModifierName>VictoryRule.java                词条相关胜利规则（如恋人）
 * }</pre>
 *
 * <h2>词条 vs 职业</h2>
 * <ul>
 *   <li>词条可以随机挂到任意玩家身上（除非用职业名单限制），职业是身份。</li>
 *   <li>词条显示在旁观 / 创造模式的玩家名牌上，但<b>不会</b>出现在本能透视里。</li>
 *   <li>词条注册在 {@code registry/FangRolesModifierRegistry}，用
 *       {@code HMLModifiers.registerModifier(...)}。</li>
 * </ul>
 *
 * <h2>分配规则必须用 API</h2>
 * <p>词条与职业的绑定 / 排斥、同玩家词条互斥、强制配对、动态词条上限，
 * 统一使用 {@code org.agmas.harpymodloader.api.assignment.ModifierAssignmentApi}：
 * <ul>
 *   <li>{@code registerModifierRequiresRole(...)} / {@code registerModifierExcludesRole(...)}</li>
 *   <li>{@code registerModifierMutualExclusion(...)} / {@code registerModifierOneWayExclusion(...)}</li>
 *   <li>{@code registerBeforeAssignmentHandler(...)} /
 *       {@code registerBeforeAnnouncementHandler(...)} /
 *       {@code registerAfterAssignmentHandler(...)}</li>
 * </ul>
 * <b>不要</b>新增 {@code ModdedMurderGameMode#assignModifiers} 的 mixin。</p>
 *
 * <h2>客户端对应目录</h2>
 * <pre>{@code
 * src/client/java/org/agmas/fangroles/client/modifiers/<modifier_id>/
 * src/client/java/org/agmas/fangroles/client/hud/modifiers/<modifier_id>/<ModifierName>Hud.java
 * src/client/java/org/agmas/fangroles/client/ui/modifiers/<modifier_id>/<ModifierName>InventoryButtons.java
 * }</pre>
 */
package org.agmas.fangroles.modifiers;
