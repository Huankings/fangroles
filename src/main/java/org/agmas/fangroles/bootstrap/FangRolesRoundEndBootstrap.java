package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 结算阵营迁移 API 的分类注册入口。
 *
 * <h2>为什么需要这一层</h2>
 * <p>职业的真实阵营在注册时确定，但某些词条、伪装或结算状态需要在“结算画面”里把玩家
 * 归入另一个普通栏目。{@code RoundEndFactionApi} 在服务端生成结算快照时解析最终栏目，
 * 扩展不应再 mixin {@code RoundTextRenderer}，也不应改 {@code RoundEndData} 构造或自行发结算包。</p>
 *
 * <h2>迁移的边界</h2>
 * <ul>
 *   <li>迁移只影响本次结算快照、普通阵营个人胜负、结算音效和回放 {@code is_winner}；
 *       不会改变玩家真实职业、实时阵营、杀手能力、商店、同伙关系或对局结束条件。</li>
 *   <li>真实职业名称 / 颜色、头像和死亡红叉仍沿用原职业数据，迁移不会伪装职业身份。</li>
 *   <li>规则应读取结算时仍然有效的词条状态，不要以“玩家仍然存活”为前提，
 *       否则带词条的死者会错误回到原阵营。</li>
 *   <li>独立胜利与普通共胜仍由 {@code VictoryApi} / {@code CustomVictory} 仲裁，
 *       本 API 只处理普通栏目归属。</li>
 * </ul>
 *
 * <h2>本类只做编排</h2>
 * <p>具体规则放 {@code roles/<role_id>/<RoleName>RoundEndHandler} 或
 * {@code modifiers/<modifier_id>/<ModifierName>RoundEndHandler}，本类只调用它们的 {@code init()}。
 * 规则在服务端快照阶段执行，不需要客户端类，也不需要新增网络包。</p>
 */
public final class FangRolesRoundEndBootstrap {
    private static boolean initialized;

    private FangRolesRoundEndBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的结算阵营迁移规则。
     *
     * <p>新增规则时在这里调用对应所有者的 {@code *RoundEndHandler.init()}，
     * 不要把具体职业判断写进本聚合类。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // 目前没有需要迁移结算栏目的职业或词条，保留空入口。
        // 后续在这里调用各所有者的 *RoundEndHandler.init()。
        FangRolesCore.LOGGER.debug("[fangroles] 结算阵营迁移 API 接入完成（当前为空）");
    }
}
