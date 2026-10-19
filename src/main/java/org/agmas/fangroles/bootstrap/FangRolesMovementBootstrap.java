package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 玩家体力与移动速度 API 的总入口。
 *
 * <h2>必须走公开 API，不要写 mixin</h2>
 * <p>Wathe 已经公开了这几块能力：</p>
 * <ul>
 *   <li>体力读写：{@code dev.doctor4t.wathe.api.stamina.PlayerStaminaApi} /
 *       {@code dev.doctor4t.wathe.cca.PlayerStaminaComponent}</li>
 *   <li>有限体力免消耗：{@code PlayerStaminaApi.registerConsumptionRule(...)} 返回
 *       {@code PASS} / {@code CONSUME} / {@code SKIP}；普通能力成本用
 *       {@code consumeStamina(player, amount, CUSTOM)}</li>
 *   <li>速度修正：{@code dev.doctor4t.wathe.api.movement.PlayerMovementApi#registerSpeedModifier}</li>
 * </ul>
 * <p>扩展<b>不要</b>再 shadow {@code forwardSpeed} / {@code sidewaysSpeed}，
 * 也不要再 mixin {@code travel()} / {@code jump()} / {@code getMovementSpeed()} 来覆盖全局逻辑，
 * 否则会与 Wathe 自己的体力、心情惩罚、速度叠加互相打架。</p>
 *
 * <h2>速度修正规则写法</h2>
 * <pre>{@code
 * PlayerMovementApi.registerSpeedModifier(
 *         FangRolesCore.id("vanisher_speed"),
 *         100,                                   // priority 越大越先执行
 *         context -> {
 *             // 只对「本人 + 本职业 + 特定状态」生效，其余情况必须返回 PASS 让低优先级继续处理
 *             if (!isVanisherInvisible(context.player())) {
 *                 return PlayerMovementApi.Result.pass();
 *             }
 *             // MULTIPLY 表示在已有速度上乘算；ADD 表示加算；OVERRIDE 表示直接覆盖（慎用）
 *             return PlayerMovementApi.Result.multiply(1.2f);
 *         });
 * }</pre>
 *
 * <h2>体力操作注意事项</h2>
 * <ul>
 *   <li>改体力上限用 {@code PlayerStaminaApi} 的上限修正接口，不要直接写组件字段，
 *       否则回合重置时容易残留。</li>
 *   <li>「本局初始化标记」也在组件里，新职业需要在开局时调用 {@code PlayerStaminaApi}
 *       的重置入口，而不是自己维护一个布尔值。</li>
 *   <li>体力归零时 Wathe 会拦截自主水平移动与跳跃，但击退、传送、水流、载具等外力位移仍然保留。
 *       职业如果要求「完全不能动」，需要额外处理，不能只依赖体力归零。</li>
 * </ul>
 *
 * <h2>有限体力免消耗（不要再用“巨大上限 + 每 tick 回满”绕过）</h2>
 * <p>职业在疯魔、技能或词条期间想暂时不消耗体力时，注册
 * {@code PlayerStaminaApi.registerConsumptionRule(id, priority, rule)}：</p>
 * <ul>
 *   <li>{@code PASS}：继续询问低优先级规则；全部 PASS 时正常消耗。</li>
 *   <li>{@code CONSUME}：强制本次正常消耗，不再询问更低优先级。</li>
 *   <li>{@code SKIP}：跳过本次正常消耗，但不会回满、不会提高上限。</li>
 * </ul>
 * <p>疾跑与低落走路分别由 Wathe 以 {@code SPRINT} / {@code WALK} 调用；
 * 扩展主动声明的能力成本应调用 {@code consumeStamina(player, amount, CUSTOM)}。
 * 管理员调试、惩罚技能或明确要无视免消耗的逻辑仍使用
 * {@code drainStamina / clearStamina / setStamina}，这些入口不会被规则拦截。
 * 免消耗不解除低心情禁跑，也不解除体力归零后的移动 / 跳跃限制。</p>
 * <p>纯消耗规则可命名为 {@code roles/<role_id>/<RoleName>StaminaHandler}，
 * 仍由本类统一调用 {@code init()}，不要另建一套每 tick 补偿逻辑。</p>
 *
 * <p>数值要求：体力、速度倍率、持续时间等玩法数值一律放进对应职业的 {@code *Constants} 类，
 * 不要写成散落的 magic number。</p>
 */
public final class FangRolesMovementBootstrap {
    private FangRolesMovementBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的体力与速度修正。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>MovementHandler.init()}，
     * <b>不要</b>把具体判定逻辑写进本类。本类只做编排。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 移动与体力 API 接入完成（当前为空）");
    }
}
