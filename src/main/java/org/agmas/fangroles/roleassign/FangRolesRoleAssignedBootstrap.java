package org.agmas.fangroles.roleassign;

import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.fangroles.roles.poisoner.PoisonerRoleAssignedHandler;
import org.agmas.fangroles.sync.FangRolesPlayerComponentSyncHandlers;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;

/**
 * fangroles 的 Harpy 角色 / 词条分配接入总引导。
 *
 * <h2>本类负责三件事</h2>
 * <ol>
 *   <li><b>分配后初始化</b>：监听 Harpy {@code ModdedRoleAssigned} / {@code ModifierAssigned}，
 *       给玩家发开局物品、重置本局状态、设置开局冷却。</li>
 *   <li><b>移除时清理</b>：监听 {@code ModdedRoleRemoved} / {@code ModifierRemoved}。
 *       注意：如果加的是 Attribute 这类会留在玩家身上的持久状态，
 *       应该改用 {@code ResetPlayerEvent}（它在开局前和结束各触发一次，能覆盖掉线重连）。</li>
 *   <li><b>分配规则</b>：调用各职业自己的
 *       {@code roles/<role_id>/<RoleName>RoleAssignmentRules.init()}
 *       和词条的 {@code modifiers/<modifier_id>/<ModifierName>ModifierAssignmentRules.init()}。</li>
 * </ol>
 *
 * <h2>Harpy 分配规则 API（不要写 mixin）</h2>
 * <ul>
 *   <li>职业同局互斥 / 单向排斥：
 *       {@code RoleAssignmentApi.registerMutualExclusion(...)}、
 *       {@code registerOneWayExclusion(...)}</li>
 *   <li>职业绑定生成或阶段末补位：
 *       {@code RoleAssignmentApi.registerBeforePhaseHandler(...)} /
 *       {@code registerAfterPhaseHandler(...)}，
 *       阶段为 {@code CIVILIAN_REPLACEMENT} / {@code VIGILANTE_REPLACEMENT} /
 *       {@code KILLER_REPLACEMENT}；写最终职业用
 *       {@code RoleAssignmentPhaseContext.assignRole(...)}</li>
 *   <li>词条与职业绑定 / 排斥：
 *       {@code ModifierAssignmentApi.registerModifierRequiresRole(...)}、
 *       {@code registerModifierExcludesRole(...)}</li>
 *   <li>同玩家词条互斥：
 *       {@code registerModifierMutualExclusion(...)} /
 *       {@code registerModifierOneWayExclusion(...)}</li>
 *   <li>强制配对、动态词条上限等替代 {@code assignModifiers} mixin 的逻辑：
 *       {@code registerBeforeAssignmentHandler(...)} /
 *       {@code registerBeforeAnnouncementHandler(...)} /
 *       {@code registerAfterAssignmentHandler(...)}</li>
 * </ul>
 * <p>除非 Harpy API 确实无法表达需求，<b>不要</b>新增
 * {@code ModdedMurderGameMode#findAndAssignPlayers}、{@code assignModifiers}
 * 或权重相关的 mixin。</p>
 *
 * <h2>文件拆分约定（NoellesRoles 风格，必须遵守）</h2>
 * <pre>{@code
 * roles/<role_id>/<RoleName>RoleAssignmentRules.java
 * modifiers/<modifier_id>/<ModifierName>ModifierAssignmentRules.java
 * }</pre>
 * <p>本类只调用各自的 {@code init()}，不要把多个职业的规则塞进一个公共大类。</p>
 */
public final class FangRolesRoleAssignedBootstrap {
    private FangRolesRoleAssignedBootstrap() {
    }

    /**
     * 注册分配事件监听与所有职业 / 词条的分配规则。
     *
     * <p>调用时机必须在职业与词条注册完成之后，见 {@code FangRolesBootstrap} 的顺序说明。</p>
     */
    public static void init() {
        // 毒师分配后设置毒针 30 秒开局冷却。
        ModdedRoleAssigned.EVENT.register(PoisonerRoleAssignedHandler::onRoleAssigned);

        /*
         * 转职后观察者权限可能刚变化，但目标组件本身没有发生状态变化。
         * 这里统一补发一次当前追踪目标；具体需要刷新哪些组件由各职业
         * *ComponentSyncHandler 通过 FangRolesPlayerComponentSyncHandlers 注册。
         */
        ModdedRoleAssigned.EVENT.register((player, role) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                FangRolesPlayerComponentSyncHandlers.syncTrackedTargetsAfterRoleAssigned(serverPlayer);
            }
        });

        FangRolesCore.LOGGER.debug("[fangroles] 角色分配接入完成");
    }
}
