package org.agmas.fangroles.registry;

import dev.doctor4t.wathe.api.Faction;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;

/**
 * fangroles 的职业 {@link Role} 实例注册表。
 *
 * <p>本类职责：把 {@link FangRolesIds} 里登记的 id 变成 Wathe 认识的 {@code Role} 对象，
 * 并<b>显式登记阵营</b>。Harpy 的扩展职业分配按 {@code role.getFaction()} 划分
 * 平民 / 义警 / 杀手 / 中立四个池子，所以阵营必须显式写清楚，
 * 不要只靠七参构造里的 {@code isInnocent} / {@code canUseKiller} 让 Wathe 去猜。</p>
 *
 * <h2>Wathe 七参构造参数含义（顺序不能错）</h2>
 * <ol>
 *   <li>{@code Identifier identifier}：职业 id，用 {@link FangRolesCore#id(String)} 生成</li>
 *   <li>{@code int color}：职业色，{@code 0xRRGGBB}。建议放职业自己的 {@code *Constants}</li>
 *   <li>{@code boolean isInnocent}：被枪击时是否掉枪；同时在胜利条件里是否算平民</li>
 *   <li>{@code boolean canUseKiller}：是否拥有杀手能力（可见 / 可用杀手功能）</li>
 *   <li>{@code MoodType moodType}：心情真假，平民/义警/中立一般 {@code REAL}，杀手一般 {@code FAKE}</li>
 *   <li>{@code int maxSprintTime}：最大疾跑时间（tick）。通常直接借用同阵营原版职业的值</li>
 *   <li>{@code boolean canSeeTime}：是否能看到顶部时间</li>
 * </ol>
 *
 * <h2>四个阵营的登记入口</h2>
 * <ul>
 *   <li>平民阵营：{@link WatheRoles#registerCivilianRole(Role)}</li>
 *   <li>义警阵营：{@link WatheRoles#registerVigilanteRole(Role)}（会替换原版义警位，不要混进普通平民池）</li>
 *   <li>杀手阵营：{@link WatheRoles#registerKillerRole(Role)}</li>
 *   <li>中立阵营：{@link WatheRoles#registerNeutralRole(Role)}</li>
 * </ul>
 *
 * <h2>新增职业的完整范例（照抄改名字即可）</h2>
 * <pre>{@code
 * // 1) FangRolesIds.java 里先登记
 * public static final Identifier VANISHER_ID = FangRolesCore.id("vanisher");
 *
 * // 2) 本类里创建 Role 并显式登记阵营
 * public static final Role VANISHER = WatheRoles.registerCivilianRole(new Role(
 *         FangRolesIds.VANISHER_ID,
 *         0x8A2BE2,                                        // 职业色
 *         true,                                            // 平民：枪会掉落
 *         false,                                           // 没有杀手能力
 *         Role.MoodType.REAL,                              // 真实心情
 *         WatheRoles.CIVILIAN.getMaxSprintTime(),          // 沿用平民的疾跑时间
 *         false                                            // 看不到时间
 * ));
 *
 * // 3) bootstrap/FangRolesLimitsBootstrap.java 里限制最大生成数
 * //    Harpymodloader.setRoleMaximum(VANISHER, 1);
 *
 * // 4) roles/vanisher/ 下建职业包，并在 roles/vanisher/VanisherRoleAssignmentRules.java
 * //    里写 Harpy 同局互斥 / 绑定生成规则
 *
 * // 5) lang/zh_cn.json + lang/en_us.json 补两条翻译（Harpy 会自动生成 announcement，
 * //    但 Noelles 风格仍要求自己补，避免显示成原始 key）：
 * //    "announcement.role.fangroles.vanisher": "消失者",
 * //    "announcement.goals.fangroles.vanisher": "在被发现之前让所有人消失"
 * }</pre>
 *
 * <p><b>注意</b>：本类当前刻意不注册任何职业。模板阶段注册空职业会让 Harpy 日志出现
 * 无翻译、无上限、无分配规则的职业，反而不利于后续排查。</p>
 */
public final class FangRolesRoleRegistry {

    // ---------------------------------------------------------------------
    // 职业实例定义区（当前为空）
    //
    // 新增职业时把范例外面的 `public static final Role XXX = ...` 写在这里。
    // 顺序建议：平民 → 义警 → 杀手 → 中立，和 Wathe 的注册顺序保持一致，便于比对。
    // ---------------------------------------------------------------------

    private FangRolesRoleRegistry() {
    }

    /**
     * 显式初始化入口。
     *
     * <p>职业实例是 {@code static final} 字段，在类被首次访问时就会完成注册。
     * 提供这个方法是为了让 {@code FangRolesBootstrap} 能明确地触发类加载，
     * 从而保证注册顺序稳定（而不是依赖某个其它类碰巧先引用了这里）。</p>
     *
     * <p>后续如果需要在注册后做额外处理（例如给某职业补 {@link Faction} 覆写、
     * 或注册跨系统分组），也统一写在这个方法里。</p>
     */
    public static void init() {
        // 触发类加载：静态字段的职业注册在这一刻完成。
        // 目前没有职业，所以这里是空实现；请勿删除本类与 FangRolesBootstrap 中的调用。
    }
}
