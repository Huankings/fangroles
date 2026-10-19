package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 动态玩家心情类型 API 的分类注册入口。
 *
 * <h2>为什么需要这一层</h2>
 * <p>{@code Role#getMoodType()} 只是职业注册时写入的共享基础值，同职业所有玩家共用。
 * 词条、职业阶段或临时状态需要把某个玩家单独从 REAL 转成 FAKE / NONE（或反向转换）时，
 * 必须注册 {@code PlayerMoodTypeApi} resolver，不能直接改 Role 字段：
 * Role 是全局注册对象，直接改会把同职业其他玩家一起污染，也无法安全同步到客户端。</p>
 *
 * <h2>本类只做编排</h2>
 * <p>具体规则放 {@code roles/<role_id>/<RoleName>MoodTypeHandler} 或
 * {@code modifiers/<modifier_id>/<ModifierName>MoodTypeHandler}，本类只调用它们的 {@code init()}。
 * resolver 同时运行在服务端与客户端 HUD，判断条件必须来自两端已经同步的职业 / 词条 / CCA 状态，
 * 不能依赖只在服务端存在的静态 Map，也不能在 resolver 内改组件、发任务、发包或递归解析类型。</p>
 *
 * <p>Wathe 服务端的心情下降、任务奖励、阈值补槽、精神崩溃和任务发放资格，以及客户端 HUD，
 * 都会读取解析后的有效类型；HUD 扩展应使用 {@code MoodHudContext.effectiveMoodType()}，
 * 不要再直接读 {@code role().getMoodType()}。</p>
 */
public final class FangRolesMoodTypeBootstrap {
    private static boolean initialized;

    private FangRolesMoodTypeBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的心情类型 resolver。
     *
     * <p>新增转换规则时在这里调用对应所有者的 {@code *MoodTypeHandler.init()}，
     * 不要把具体职业判断写进本聚合类。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // 目前没有职业或词条需要动态转换心情类型，保留空入口。
        // 后续在这里调用各所有者的 *MoodTypeHandler.init()，不要把具体判定写进本聚合类。
        FangRolesCore.LOGGER.debug("[fangroles] 动态心情类型 API 接入完成（当前为空）");
    }
}
