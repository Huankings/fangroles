package org.agmas.fangroles.bed;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 床效果（Bed effect）注册入口。
 *
 * <p>床是 Wathe 的睡眠 / 恢复载体。扩展要给床加自定义效果时走
 * {@code dev.doctor4t.wathe.api.bed.BedEffectRegistry}，
 * <b>不要</b> mixin 床相关方块或 BlockEntity。</p>
 *
 * <h2>服务端效果 / 客户端粒子是两层，不要混为一谈</h2>
 * <table border="1">
 *   <caption>床系统的两层职责</caption>
 *   <tr><th>层</th><th>入口</th><th>负责什么</th></tr>
 *   <tr>
 *     <td>服务端</td>
 *     <td>{@code BedEffectRegistry}（本类）</td>
 *     <td>识别塞床物品、能否放置、效果与归属者同步到床头方块实体、
 *         睡觉后 40 tick 的统一结算、给回放保留 {@code bed_effect} 字段。</td>
 *   </tr>
 *   <tr>
 *     <td>客户端</td>
 *     <td>{@code BedParticleRegistry}（{@code client/bed/FangRolesBedParticles}）</td>
 *     <td>只负责「这张床上显示什么粒子」。纯视觉，不影响触发、所有权、消耗和回放。</td>
 *   </tr>
 * </table>
 * <p>两者用<b>同一个效果 id 字符串</b>串起来：
 * 本类注册效果时用的 {@code Identifier}，会被
 * {@code BedEffectRegistry.applyStandardEffect(...)} 写进床方块实体的
 * {@code bed_effect} 字段；客户端 provider 再通过
 * {@code context.bed().getBedEffect()} 读回来比较。
 * 所以这个 id <b>必须</b>登记在 {@code registry/FangRolesEventIds}，
 * 让服务端注册处和客户端判断处引用同一个常量 —— 手写字符串不一致是
 * 「注册了但没生效」最常见的原因。</p>
 *
 * <h2>接入要点</h2>
 * <ul>
 *   <li><b>注册效果</b>：实现 {@code BedEffectHandler}（含 {@code effectId()}、
 *       {@code additiveItem()}、{@code canApply(...)}、{@code applyToBed(...)}、
 *       {@code onSleepTrigger(...)}），用 {@code BedEffectRegistry.register(handler)} 注册。</li>
 *   <li><b>标准塞床逻辑直接复用</b>：不要自己写一遍扣物品、播放音效和写回放。
 *       用 {@code BedEffectRegistry.applyStandardEffect(player, heldStack, bed, pos,
 *       effectId, replaceExistingEffect)}，需要额外回放字段时用带
 *       {@code replayExtra} 的那个重载。</li>
 *   <li><b>查床</b>：{@code BedEffectRegistry.resolveHead(world, pos)} 把任意一半床坐标
 *       解析成床头方块实体；{@code findTriggeredBedEffect(world, centerPos)} 复用与本体
 *       一致的搜索逻辑（半径 2 的 3D Bresenham 视线检查）。</li>
 *   <li><b>持续型效果不要每 tick 全量同步给所有人</b>。
 *       参考 {@code roles/noisemaker} 或 {@code modifiers/sedative} 的
 *       「只在状态变化边界同步」写法。</li>
 *   <li><b>客户端表现</b>：床粒子在 {@code client/bed/FangRolesBedParticles} 里注册
 *       （由 {@code client/bootstrap/FangClientParticleBootstrap} 调用），
 *       不要在职业 handler 里各自调 {@code BedParticleRegistry}。
 *       音效、屏幕效果则放对应客户端职业包。</li>
 *   <li><b>回合清理</b>：如果效果有持续时间或跨 tick 状态，
 *       必须纳入回合结束清理（见 {@code bootstrap/FangRolesEventBootstrap}）。</li>
 * </ul>
 *
 * <h2>与商店 / 物品的关系</h2>
 * <p>如果床效果是由购买商品触发的，请用 {@code ShopEntry.action(...)}
 * （{@code PurchaseType.ACTION}），不要给玩家一个「用来右键床」的假物品。
 * 但要注意：{@code BedEffectRegistry} 是按<b>主手物品</b>识别塞床动作的
 * （{@code getByAdditiveItem(...)} 走 {@code IdentityHashMap}），
 * 所以「塞床」这个动作用的是真实物品，而「购买直接生效」才用 {@code ACTION}，
 * 两者不要混淆。</p>
 */
public final class FangRolesBedEffects {
    private FangRolesBedEffects() {
    }

    /**
     * 注册所有床效果（服务端）。
     *
     * <p>新增效果时在这里注册，并写中文注释说明：效果语义、触发时机、
     * 对应的床粒子 handler 在哪个客户端包里。
     * 效果 id 请从 {@code registry/FangRolesEventIds} 取，不要手写字符串。</p>
     */
    public static void register() {
        // 目前没有床效果，空实现。
        // 新增示例（取消注释前请先建好 BedEffectHandler 与 EventIds 常量）：
        // BedEffectRegistry.register(new SomeBedEffectHandler());
        FangRolesCore.LOGGER.debug("[fangroles] 床效果注册完成（当前为空）");
    }
}
