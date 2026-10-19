/**
 * fangroles 的物品实现区。
 *
 * <p>本包放<b>带行为的物品类</b>，例如 {@code VanisherDaggerItem}。
 * 单纯的注册实例化放在 {@code ModItems}，不要把业务逻辑写回那里。</p>
 *
 * <h2>武器类物品的接入要求</h2>
 * <ul>
 *   <li>近战 / 枪械的命中判定不要自己写 {@code ProjectileUtil} 射线。
 *       客户端准备目标用 {@code WeaponTargetingApi}，
 *       服务端接管命中用 Wathe {@code GunShotApi}。</li>
 *   <li>物品需要有「使用姿势」时，参考 NoellesRoles 的 mixin 写法
 *       （例如 {@code MyersButcherUsePoseMixin}），
 *       但只在 API 无法表达时才写。</li>
 *   <li>锁栏 / 防丢弃不要自己写 {@code ServerPlayNetworkHandler} 或
 *       {@code PlayerInventory} 的 mixin，
 *       优先用 Wathe 已有的 profile / handler 机制。</li>
 * </ul>
 *
 * <h2>冷却显示</h2>
 * <ul>
 *   <li>标准物品描述与冷却读秒用客户端 {@code ItemTooltipApi.registerItem(...)} /
 *       {@code registerItems(...)}。</li>
 *   <li>读秒必须读取当前 {@code ItemCooldownManager.Entry} 的
 *       {@code endTick - tick}，<b>禁止</b>用
 *       {@code GameConstants.ITEM_COOLDOWNS * getCooldownProgress(...)} 反推，
 *       否则 GunShotApi modifier、开局冷却和多来源冷却都会显示错误。</li>
 *   <li>真正的动态额外说明用 {@code ItemTooltipApi.registerAppender(...)}。</li>
 *   <li>tooltip 只是客户端显示，<b>不得</b>作为服务端购买、攻击或能力合法性判断依据。</li>
 * </ul>
 *
 * <h2>资源文件</h2>
 * <pre>{@code
 * src/main/resources/assets/fangroles/models/item/<name>.json
 * src/main/resources/assets/fangroles/textures/item/<name>.png
 * lang/zh_cn.json + lang/en_us.json → "item.fangroles.<name>"
 * }</pre>
 */
package org.agmas.fangroles.item;
