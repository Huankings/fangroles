package org.agmas.fangroles.registry;

import net.minecraft.util.Identifier;

/**
 * fangroles 的稳定「事件 id」登记处。
 *
 * <p>这里集中管理所有需要跨系统引用的字符串 id，避免同一个 id 在
 * 回放注册、护盾规则、托盘效果、床效果、疯魔 profile 等多处被手写重复，
 * 一旦写法不一致就会出现「注册了但没生效」的隐蔽 bug。</p>
 *
 * <p>本类通常包含以下几类：</p>
 * <ul>
 *   <li><b>回放事件 id</b>：配合 {@code record/FangRolesReplayFormatters} 使用，
 *       回放数据里只存稳定 id，展示文字走 {@code Text.translatable(...)}，
 *       不要往回放数据里塞中文句子。</li>
 *   <li><b>托盘 / 床效果 id</b>：配合 {@code tray/FangRolesTrayEffects}、
 *       {@code bed/FangRolesBedEffects} 使用。</li>
 *   <li><b>疯魔 profile id</b>：配合 {@code bootstrap/FangRolesPsychoBootstrap} 与
 *       Wathe {@code PsychoModeApi.start(player, profileId)} 使用。</li>
 *   <li><b>护盾来源 id</b>：配合 Wathe {@code PsychoModeApi.registerShieldRule(...)} 使用。</li>
 * </ul>
 *
 * <h2>新增范例</h2>
 * <pre>{@code
 * // 回放事件：某职业发动了能力
 * public static final Identifier ABILITY_USED_ID = FangRolesCore.id("ability_used");
 *
 * // 疯魔 profile：某职业的专属疯魔形态
 * public static final Identifier RAGE_MODE_ID = FangRolesCore.id("rage_mode");
 * }</pre>
 *
 * <p>本类当前为空，等待第一个职业。</p>
 */
public final class FangRolesEventIds {

    // ---------------------------------------------------------------------
    // 事件 / 效果 / profile id 定义区（当前为空）
    // ---------------------------------------------------------------------

    private FangRolesEventIds() {
    }
}
