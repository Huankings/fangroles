package org.agmas.fangroles.combat;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 枪击 / 匕首冷却 / 武器目标 API 的总引导。
 *
 * <h2>服务端：枪击接管</h2>
 * <p>统一使用 {@code dev.doctor4t.wathe.api.combat.GunShotApi}：
 * <ul>
 *   <li>接管某职业开火后果：{@code GunShotApi.registerShotHandler(...)}</li>
 *   <li>覆写左轮目标：{@code GunShotContext} / {@code GunTargetContext}</li>
 *   <li>左轮误伤惩罚：{@code RevolverPenaltyContext}</li>
 *   <li>开火后冷却修正：{@code GunShotApi.registerCooldownModifier(...)}</li>
 * </ul>
 * <b>不要</b>再 mixin {@code GunShootPayload}、{@code RevolverItem} 或 {@code DerringerItem}
 * 的通用流程 —— 那是旧扩展的做法，会和其它职业的接管互相覆盖。</p>
 *
 * <h2>服务端：匕首冷却</h2>
 * <p>Wathe 默认匕首刺杀后的冷却修正统一走 {@code dev.doctor4t.wathe.api.combat.KnifeCooldownApi}：</p>
 * <ul>
 *   <li>注册：{@code KnifeCooldownApi.registerCooldownModifier(id, priority, modifier)}</li>
 *   <li>modifier 是流水线：priority 越大越先执行，后续规则读取前一步已经修正的
 *       {@code currentCooldown}；最终结果自动限制为非负数。</li>
 *   <li>只缩短冷却时返回 {@code Math.min(currentCooldown, targetTicks)}，
 *       不要覆盖其它规则已经缩短的结果。</li>
 *   <li>{@code KnifeCooldownContext} 提供攻击者、目标、服务端物品快照、对局组件、
 *       基础冷却和默认是否会真正写入冷却；modifier 在死亡流程返回后执行，
 *       因此可以消费 DeathApi 确认死亡阶段写入的一次性标记。</li>
 * </ul>
 * <p><b>不要</b>再 mixin {@code KnifeStabPayload.Receiver} 的尾部重写冷却。</p>
 *
 * <h2>客户端：武器射线目标</h2>
 * <p>统一使用 {@code WeaponTargetingApi}，按「视觉目标 / 真实攻击」选择入口：</p>
 * <ul>
 *   <li>准心、HUD、玩家名、锁定提示：{@code getVisibleAlivePlayerTarget(player, range)}</li>
 *   <li>普通近战或物品真实命中：{@code getAttackableAlivePlayerTarget(player, range)}</li>
 *   <li>枪械准心：{@code resolveVisibleGunTarget(player, stack, range)}</li>
 *   <li>枪械真实发包：{@code resolveAttackableGunTarget(player, stack, range)}</li>
 * </ul>
 * <p>这四个入口已经分别处理 {@code TARGET} 和 {@code ATTACK} 语义，
 * 枪械入口还会继续经过 {@code GunShotApi} 的目标覆写链。
 * <b>不要</b>在本模组里重新拼 {@code ProjectileUtil} + 存活判断 + 可见性判断，
 * 也不要自己维护一套目标选择 helper。</p>
 *
 * <h2>最容易犯的语义错误</h2>
 * <p>把「视觉上隐藏目标（TARGET）」误写成「攻击免疫（ATTACK）」。
 * 尸体伪装可以隐藏准心变化，但不会让目标获得攻击免疫。
 * 如果只是想让准心看不见，请只改 TARGET 入口。</p>
 *
 * <h2>服务端仍然必须重新校验</h2>
 * <p>客户端目标只负责准备实体 id。服务端 packet / {@code GunShotContext}
 * 仍必须重新校验职业、存活、冷却、距离和
 * {@code TargetVisibilityApi.canAttackPlayer(...)}。</p>
 */
public final class FangRolesCombatBootstrap {
    private FangRolesCombatBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的枪击、匕首冷却与武器目标规则。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>GunHandler.init()}、
     * {@code <RoleName>GunCooldownHandler.init()} 或
     * {@code <RoleName>KnifeCooldownHandler.init()}，本类只做编排。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 枪击 / 匕首冷却 / 武器目标 API 接入完成（当前为空）");
    }
}
