package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端移动表现注册入口。
 *
 * <p>与 {@code bootstrap/FangRolesMovementBootstrap}（服务端体力 / 速度修正）配套。
 * 本类只处理<b>客户端表现</b>：视野变化、相机偏移、步伐音效、屏幕效果。</p>
 *
 * <h2>职责边界（很重要）</h2>
 * <ul>
 *   <li><b>真实速度与体力由服务端裁定</b>：走 {@code PlayerMovementApi} /
 *       {@code PlayerStaminaApi}。客户端不要自己改本地速度，否则会与服务端不同步，
 *       表现为「本地跑得快但服务端判定没动」的回弹。</li>
 *   <li><b>本类只做观感</b>：例如疾跑时轻微 FOV 变化、职业专属脚步声、
 *       狂暴状态的屏幕边缘效果。</li>
 *   <li><b>FOV / 相机改动要可叠加且可恢复</b>：
 *       必须用乘算或保存原值恢复，不要直接赋值，
 *       否则会破坏玩家自己的 FOV 设置和原版疾跑 FOV 效果。</li>
 * </ul>
 *
 * <h2>必须考虑的边界</h2>
 * <ul>
 *   <li>玩家死亡 / 旁观 / 创造模式时不能继续应用职业表现。</li>
 *   <li>被控制、被绑架、处于眩晕等「输入被接管」状态时不应该叠加自己的效果。</li>
 *   <li>本局结束后要立刻恢复（由 {@code FangClientConnectionBootstrap}
 *       与组件同步变化共同保证）。</li>
 * </ul>
 */
public final class FangClientMovementBootstrap {
    private FangClientMovementBootstrap() {
    }

    /**
     * 注册所有客户端移动表现。
     *
     * <p>目前没有职业移动表现，空实现。
     * 新增时在这里调用该职业自己的
     * {@code client/roles/<role_id>/<RoleName>MovementClientHandler.init()}。</p>
     */
    public static void init() {
        // 目前没有移动表现，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端移动表现注册完成（当前为空）");
    }
}
