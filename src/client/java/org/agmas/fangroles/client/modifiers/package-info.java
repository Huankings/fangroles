/**
 * fangroles 的词条客户端实现区。
 *
 * <h2>目录约定</h2>
 * <pre>{@code
 * client/modifiers/<modifier_id>/
 *   <ModifierName>ClientState.java     纯客户端缓存
 *   <ModifierName>ClientHandler.java   词条的客户端表现（模型、皮肤、输入限制）
 * }</pre>
 * <p>词条的固定屏幕 HUD 放在
 * {@code client/hud/modifiers/<modifier_id>/<ModifierName>Hud.java}；
 * 词条的背包按钮放在
 * {@code client/ui/modifiers/<modifier_id>/<ModifierName>InventoryButtons.java}。</p>
 *
 * <h2>词条客户端表现的两个常见需求</h2>
 * <ol>
 *   <li><b>模型 / 皮肤可见性</b>（例如变色龙让披风或手臂消失）：
 *       需要客户端 mixin 时，注册到
 *       {@code src/client/resources/fangroles.client.mixins.json}，
 *       并且注入条件要尽量窄。</li>
 *   <li><b>输入限制</b>（例如双重人格休眠期间禁止某些操作）：
 *       同样属于客户端输入拦截，但服务端仍然要独立校验，
 *       不能因为客户端没发包就认为玩家没有该能力。</li>
 * </ol>
 *
 * <h2>注意</h2>
 * <p>词条不会出现在本能透视里，因此不要在本包试图通过本能显示词条。</p>
 */
package org.agmas.fangroles.client.modifiers;
