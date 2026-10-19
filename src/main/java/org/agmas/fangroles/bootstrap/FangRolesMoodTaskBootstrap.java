package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 心情任务 API 的总入口。
 *
 * <h2>用 API 注册任务，不要往 enum 里加</h2>
 * <p>新增心情任务必须用 {@code MoodTaskApi.registerTask(...)} 注册
 * {@code MoodTaskDefinition}，<b>不要</b>继续往 {@code PlayerMoodComponent.Task} 追加枚举值。
 * 旧 enum 只作为 Wathe 内置任务和旧扩展的兼容层保留。</p>
 *
 * <h2>常用入口</h2>
 * <ul>
 *   <li>注册任务定义：{@code MoodTaskApi.registerTask(...)}</li>
 *   <li>指定发放：{@code MoodTaskApi.assignTask(player, taskId)}</li>
 *   <li>随机发放 / 补槽：{@code MoodTaskApi.assignRandomTasks(...)}、
 *       {@code MoodTaskApi.fillRandomTaskSlots(...)}</li>
 *   <li>删除任务：{@code MoodTaskApi.removeTask(...)}</li>
 *   <li>按完成流程完成任务：{@code MoodTaskApi.completeTask(...)}
 *       （会加心情、写回放、触发任务完成 API）</li>
 *   <li>阻止发放：{@code MoodTaskApi.registerAssignmentRule(...)}</li>
 *   <li>阻止完成：{@code MoodTaskApi.registerCompletionRule(...)}</li>
 *   <li>任务点透视：{@code MoodTaskPointApi.registerTaskPoint(...)} /
 *       {@code registerScanHandler(...)}</li>
 * </ul>
 *
 * <h2>两个必须避免的写法</h2>
 * <ol>
 *   <li><b>不要</b>用「发放后在同一 tick 删除任务」来模拟「禁止发放」。
 *       客户端会先收到任务再收到删除，表现为任务闪现。请用 {@code registerAssignmentRule(...)}。</li>
 *   <li><b>不要</b>mixin {@code PlayerMoodComponent#completeTask(...)} 来阻止特殊状态完成任务，
 *       请用 {@code registerCompletionRule(...)}。</li>
 * </ol>
 *
 * <h2>随机池默认行为</h2>
 * <p>Wathe 内置任务默认进入随机池；<b>扩展任务默认只允许指定发放</b>，
 * 只有明确希望进入普通随机任务池时，才在任务定义里显式启用随机。
 * 任务槽位由当前世界的 {@code GameWorldComponent} 动态设置（默认 3、最低 1，可高于 3）。</p>
 */
public final class FangRolesMoodTaskBootstrap {
    private FangRolesMoodTaskBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的心情任务接入。
     *
     * <p>新增职业时在这里调用该职业自己的 {@code *MoodTaskHandler.init()}，本类只做编排。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 心情任务 API 接入完成（当前为空）");
    }
}
