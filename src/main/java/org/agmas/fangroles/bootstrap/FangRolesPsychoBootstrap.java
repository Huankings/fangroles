package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 疯魔模式（Psycho）API 的总入口。
 *
 * <h2>本类只做编排</h2>
 * <p>本类<b>只</b>调用各职业自己的 {@code roles/<role_id>/<RoleName>PsychoHandler.init()}。
 * 疯魔 profile 的定义、护盾规则、声音和皮肤全部放在对应职业包里，
 * 不要把多个职业的疯魔规则塞进本类。</p>
 *
 * <h2>接入要点</h2>
 * <ul>
 *   <li><b>基础定义</b>：{@code PsychoModeProfile.copyOf(PsychoModeApi.createDefaultProfile(), id)}
 *       继承 Wathe 默认疯魔，再覆盖需要改的字段
 *       （{@code durationTicks}、{@code armour}、{@code grantedItems}、{@code hitSound}、
 *       {@code shieldSound}、{@code backgroundSound}、{@code visualSettings}、
 *       {@code nameTranslationKey} 等）。</li>
 *   <li><b>启动 / 结束</b>：服务端用 {@code PsychoModeApi.start(player, profileId)} 启动；
 *       强制收束、回合清理、召集等场景用 {@code PsychoModeApi.stop(player, recordReplay)}。
 *       <b>不要</b>直接改 {@code psychoTicks} / {@code armour}。</li>
 *   <li><b>查询状态</b>：{@code PsychoModeApi.isActive(...)}、{@code getRemainingTicks(...)}、
 *       {@code getArmour(...)}，不要依赖 CCA 内部字段。</li>
 *   <li><b>授予物品</b>：profile 的 {@code grantedItems} 会被 Wathe 打上
 *       {@code PSYCHO_GRANTED_PROFILE} 标记，结束时只回收该 profile 授予的临时物品，
 *       因此不会误删玩家自己持有的同类物品。</li>
 *   <li><b>护盾穿透 / 强制挡盾</b>：用 {@code PsychoModeApi.registerShieldRule(id, priority, handler)}，
 *       返回 {@code BYPASS} / {@code BLOCK} / {@code PASS}。
 *       <b>不要</b>mixin {@code PlayerPsychoComponent#getArmour()}。</li>
 *   <li><b>回放名称</b>：profile 要给自己的
 *       {@code psycho_mode.fangroles.<name>} / {@code psycho_shield.fangroles.<name>} 翻译，
 *       否则回放里会显示原始 key。</li>
 * </ul>
 *
 * <h2>客户端侧对应部分</h2>
 * <p>自定义皮肤和背景音乐属于客户端，放在
 * {@code client/bootstrap/FangClientPsychoBootstrap}：
 * 皮肤走 {@code PsychoModeClientApi.registerVisualProvider(...)}，
 * 背景音乐走 {@code PsychoModeClientApi.registerBackgroundAmbience(sound, intervalTicks)}，
 * Mood HUD 样式走 {@code MoodHudApi.registerPsychoStyle(...)}。</p>
 */
public final class FangRolesPsychoBootstrap {
    private FangRolesPsychoBootstrap() {
    }

    /**
     * 注册所有职业的疯魔 profile / 护盾规则。
     *
     * <p>新增职业时在这里调用该职业自己的 {@code *PsychoHandler.init()}。</p>
     */
    public static void init() {
        // 目前没有职业，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 疯魔 API 接入完成（当前为空）");
    }
}
