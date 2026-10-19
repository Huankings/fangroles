package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的 Harpy 职业最大生成数（静态上限）注册入口。
 *
 * <h2>两种上限</h2>
 * <ul>
 *   <li><b>静态上限</b>（本类）：职业固定最多几个，例如「消失者最多 1 个」。</li>
 *   <li><b>动态上限</b>：随在线人数变化，例如「每 8 人最多 1 个」。
 *       这类逻辑不要写在本类，放到 {@code bootstrap/FangRolesEventBootstrap}
 *       的 server tick 或开局事件里，因为需要读当前人数。</li>
 * </ul>
 *
 * <h2>写法</h2>
 * <pre>{@code
 * // 两种重载都可以：传 Role 实例或传 Identifier
 * Harpymodloader.setRoleMaximum(FangRolesRoleRegistry.VANISHER, 1);
 * Harpymodloader.setRoleMaximum(FangRolesIds.VANISHER_ID, 1);
 * }</pre>
 *
 * <h2>与 Harpy 分配规则的分工</h2>
 * <ul>
 *   <li>本类只表达「最多几个」。</li>
 *   <li>「不能和某职业同时出现」「必须成对出现」「某阶段补位」这些属于分配规则，
 *       必须用 {@code org.agmas.harpymodloader.api.assignment.RoleAssignmentApi}，
 *       按职业拆到 {@code roles/<role_id>/<RoleName>RoleAssignmentRules.java}，
 *       再由 {@code roleassign/FangRolesRoleAssignedBootstrap} 统一调用。
 *       <b>不要</b>再新增 {@code ModdedMurderGameMode#findAndAssignPlayers} 或
 *       {@code assignModifiers} 的 mixin。</li>
 * </ul>
 *
 * <h2>与权重账本的关系</h2>
 * <p>职业权重账本在 Wathe 的 {@code ScoreboardRoleSelectorComponent}，
 * 开关在 {@code GameWorldComponent.enableWeights} 且默认开启。
 * 原版杀手 / 义警、中立阵营和 Harpy 具体扩展职业共用同一份 {@code RoleWeightRecord}。
 * 上限会直接影响职业进入候选池的次数，因此调上限后要用
 * Harpy {@code /roleWeights preview role <role>} 复核概率是否符合预期。</p>
 */
public final class FangRolesLimitsBootstrap {
    private FangRolesLimitsBootstrap() {
    }

    /**
     * 设置所有职业的静态最大生成数。
     *
     * <p>新增职业时在这里补一行 {@code Harpymodloader.setRoleMaximum(...)}，
     * 并写中文注释说明「为什么是这个数字」（例如：该职业强度高，每局最多 1 个）。</p>
     */
    public static void initStaticLimits() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 职业静态上限初始化完成（当前为空）");
    }
}
