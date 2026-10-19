/**
 * fangroles 的调试 / 管理指令实现区。
 *
 * <h2>已有可复用的上游指令（优先用这些，不要重复造）</h2>
 * <ul>
 *   <li>Harpy {@code /setRole <player> <role>}：默认走 {@code reset} 模式，
 *       可显式加 {@code reset} / {@code state} / {@code soft}。
 *       <ul>
 *         <li>{@code reset}：保留 Wathe 本局钥匙和信件，不传送、不播放结算，
 *             并重新发送开局公告。</li>
 *         <li>{@code state}：保留 Wathe 心情任务、金币、毒药、体力、便签等本局进度，
 *             并保留词条。</li>
 *         <li>{@code soft}：只做最轻量身份切换。</li>
 *       </ul>
 *   </li>
 *   <li>Harpy {@code /forceRole} / {@code /forceModifier}：只影响下一次 Harpy 开局，
 *       开局后清空。{@code /forceRole} 的最终职业<b>会计入权重</b>，
 *       {@code /forceModifier} 不直接改变职业权重；
 *       局内 {@code /setRole} 调试转职<b>不计入</b>权重。</li>
 *   <li>Harpy {@code /roleWeights ...}：查询与重置权重账本。</li>
 *   <li>Harpy {@code /listRoles}、{@code /setEnabledRole}、{@code /setEnabledModifier}。</li>
 *   <li>Wathe {@code /wathe:setMood}、{@code /wathe:moodStaminaPenalty}、
 *       {@code /wathe:blackout ...}、{@code /wathe:playerCollision}、
 *       {@code /wathe:shopCooldownBypass}、{@code /wathe:gameSettings weights reset}。</li>
 * </ul>
 *
 * <h2>什么时候才需要自己写指令</h2>
 * <p>只有当本模组的机制无法用上游指令验证时才新增，
 * 例如「设置本职业专属状态的数值」「强制推进某状态机阶段」。</p>
 *
 * <h2>两个实现注意点</h2>
 * <ol>
 *   <li><b>长文本用 {@code sendMessage} 而不是 {@code sendCommandFeedback}</b>。
 *       后者在部分场景下不会回显，命令看起来「没反应」，
 *       Wathe 的权重报告就踩过这个坑。</li>
 *   <li>指令参数类型优先复用上游的 {@code RoleArgumentType} /
 *       {@code ModifierArgumentType}，这样能带自动补全。</li>
 * </ol>
 */
package org.agmas.fangroles.command;
