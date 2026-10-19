package org.agmas.fangroles.record;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 回放格式化器（Replay formatter）实现区。
 *
 * <p>注册编排在 {@code bootstrap/FangRolesReplayBootstrap}；
 * 本类用于容纳实际的 formatter 实现。职业数量变多后，
 * 建议按职业拆到 {@code roles/<role_id>/<RoleName>ReplayFormatters}，
 * 再由 {@code FangRolesReplayBootstrap} 聚合调用。</p>
 *
 * <h2>回放写入门径</h2>
 * <p>服务端写事件统一走 {@code GameRecordManager}，
 * 例如 {@code recordGlobalEvent(...)}、{@code recordRoleChange(...)}。
 * <b>不要</b>自己在回放数据里塞中文句子。</p>
 *
 * <h2>回溯职业修正（重要，跨模组协作时会用到）</h2>
 * <p>如果本模组将来实现了「时间回溯 / 静默修改运行时职业表」这类机制，
 * 只改运行时职业表是不够的：Wathe 回放仍会沿用初始职业快照和此前的
 * {@code ROLE_CHANGED} 时间线。此时必须调用：</p>
 * <pre>{@code
 * GameRecordManager.recordRoleCorrection(world, playerUuid, oldRole, newRole);
 * }</pre>
 * <p>它会写入带 {@code rewind_role_restore=true} 和回溯终点 tick 的
 * {@code ROLE_CHANGED} 结构，{@code ReplayGenerator} 会把它当作职业时间线屏障：
 * 不显示修正事件本身，修正后的职业用于后续事件；
 * 位于屏障之前但 tick 晚于回溯终点的旧职业变化不会再次覆盖修正结果。</p>
 * <p>接入约束：</p>
 * <ul>
 *   <li>先恢复本模组自己的组件和运行时职业映射，<b>再</b>记录 correction。</li>
 *   <li>correction 只用于回放时间线，<b>不</b>触发 {@code ModdedRoleAssigned}、
 *       不发放职业开局物品、不初始化任务。</li>
 *   <li>正常时间线中的真实转职仍使用 {@code recordRoleChange(...)}
 *       或 {@code GameWorldComponent.addRole(...)}。</li>
 *   <li><b>不要</b>为了修回放就把 {@code GameWorldComponent} 整体加入时停快照，
 *       除非同时评估了角色变更副作用和全局配置回滚。</li>
 * </ul>
 */
public final class FangRolesReplayFormatters {
    private FangRolesReplayFormatters() {
    }

    /**
     * 注册具体的回放格式化器。
     *
     * <p>目前没有回放事件，因此为空实现。
     * 由 {@code FangRolesReplayBootstrap.register()} 调用。</p>
     */
    public static void register() {
        // 示例写法（取消注释前先补好 FangRolesEventIds 与 lang 翻译）：
        //
        // ReplayRegistry.registerFormatter(FangRolesEventIds.ABILITY_USED_ID, (world, event) -> {
        //     UUID actor = event.getUuid("actor");
        //     return Text.translatable("replay.fangroles.ability_used",
        //             ReplayNames.resolvePlayerName(world, actor));
        // });
        FangRolesCore.LOGGER.debug("[fangroles] 回放格式化器实现区为空");
    }
}
