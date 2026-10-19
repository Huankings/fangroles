package org.agmas.fangroles.registry;

import dev.doctor4t.wathe.api.Role;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * fangroles 的跨系统共享职业分组。
 *
 * <p>当一个职业集合会被多个系统同时使用（例如停电夜视分组、本能分组、
 * 商店分组、胜利分组）时，把集合放在这里，<b>不要</b>从主入口类导出，
 * 也不要让每个系统各写一份名单 —— 否则后续改一名职业要改很多处，很容易漏。</p>
 *
 * <h2>为什么会需要这个类</h2>
 * <p>NoellesRoles 里就出现过这种情况：某个职业同时属于「杀手侧中立」的停电分组、
 * 本能分组和胜利分组，如果三处各写一遍职业列表，新增职业时必然漏改。
 * 统一放在这个类里可以一眼看出「这个职业到底属于哪一边」。</p>
 *
 * <h2>新增分组范例</h2>
 * <pre>{@code
 * // 1) 这里加字段（用 LinkedHashSet 保证顺序稳定，便于日志比对）
 * public static final Set<Role> SOME_GROUP = new LinkedHashSet<>();
 *
 * // 2) 在 init() 里填充
 * SOME_GROUP.add(FangRolesRoleRegistry.SOME_ROLE);
 * }</pre>
 *
 * <p><b>不要去重坑</b>：不要用 {@code Set.of(a, b)} 来初始化这些集合。
 * {@code Set.of(...)} 一旦收到重复元素会在类初始化阶段直接抛
 * {@code IllegalArgumentException: duplicate element}，服务器直接启动失败。
 * 本类统一用 {@code new LinkedHashSet<>()} + {@code add(...)} 的写法。</p>
 *
 * <p>本类当前为空，等待第一个职业加入。</p>
 */
public final class FangRolesRoleGroups {

    // ---------------------------------------------------------------------
    // 分组定义区（当前为空）
    //
    // 后续可参考 NoellesRoles 的命名方式按语义分组，例如：
    //   public static final Set<Role> KILLER_SIDED_NEUTRALS = new LinkedHashSet<>();
    //   public static final Set<Role> INDEPENDENT_NEUTRALS   = new LinkedHashSet<>();
    // ---------------------------------------------------------------------

    private FangRolesRoleGroups() {
    }

    /**
     * 分组初始化入口。
     *
     * <p>调用时机必须在 {@link FangRolesRoleRegistry#init()} 之后，
     * 因为这里要引用已经创建好的 {@link Role} 实例。</p>
     */
    public static void init() {
        // 目前没有分组，空实现。请勿删除本类与 FangRolesBootstrap 中的调用。
    }
}
