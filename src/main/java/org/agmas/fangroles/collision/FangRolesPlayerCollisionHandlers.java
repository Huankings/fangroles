package org.agmas.fangroles.collision;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 玩家物理碰撞 API 的总入口。
 *
 * <h2>统一走 {@code PlayerCollisionApi}，不要写 mixin</h2>
 * <p><b>不要</b> mixin {@code Entity#collidesWith}、{@code EntityView#getEntityCollisions}、
 * {@code Entity#pushAwayFrom} 或 {@code LivingEntity#pushAway}。
 * Wathe 已经把这些收敛到公开 API。</p>
 *
 * <h2>三种碰撞模式</h2>
 * <ul>
 *   <li>{@code SOLID}：像实体墙一样真正阻挡移动。
 *       只在两个玩家已经重叠时保留原版轻微推挤，用于解卡。</li>
 *   <li>{@code VANILLA_PUSH}：原版 MC 玩家推挤，可以穿过但仍有轻微推挤。
 *       例如 NoellesRoles 的 FEATHER 词条。</li>
 *   <li>{@code NO_COLLISION}：完全无碰撞、无推挤，玩家之间像空气一样穿过。
 *       例如灵术师脱体后的本体。</li>
 * </ul>
 * <p>handler 返回 {@code PASS} 表示继续交给低优先级规则和 Wathe 默认规则。
 * priority 越大越先执行。</p>
 *
 * <h2>方向性（最容易搞错的地方）</h2>
 * <p>{@code PlayerCollisionContext} 是<b>有方向</b>的 {@code self -> other}。
 * 单向规则只判断 {@code self}；双向规则要判断任意一方。
 * 由于原版推挤一次会同时修改双方速度，只要任意一个方向返回 {@code NO_COLLISION}，
 * Wathe 就会取消该次推挤。</p>
 *
 * <h2>Wathe 默认规则</h2>
 * <p>仍由 {@code /wathe:playerCollision} 和 {@code /wathe:startnoCollision} 控制：
 * 对局运行中、碰撞开关开启、开局免碰撞结束、双方都是局内存活玩家时返回 {@code SOLID}；
 * 开局免碰撞窗口内返回 {@code VANILLA_PUSH}。
 * 所以职业实现「穿人」时，需要明确是否要在开局免碰撞窗口之外也生效。</p>
 */
public final class FangRolesPlayerCollisionHandlers {
    private FangRolesPlayerCollisionHandlers() {
    }

    /**
     * 注册所有职业 / 词条的碰撞规则。
     *
     * <p>新增职业时在这里调用该职业自己的
     * {@code roles/<role_id>/<RoleName>PlayerCollisionHandler.init()}，
     * 或词条的 {@code modifiers/<modifier_id>/*PlayerCollisionHandler.init()}。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 玩家碰撞 API 接入完成（当前为空）");
    }
}
