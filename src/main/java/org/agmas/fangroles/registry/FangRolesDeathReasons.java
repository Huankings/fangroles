package org.agmas.fangroles.registry;

import net.minecraft.util.Identifier;

/**
 * fangroles 专属死亡原因 id 登记处。
 *
 * <p>当职业击杀需要显示成专属文案（例如「%s被拖进了黑暗」而不是通用的「%s被杀死了」）时，
 * 在这里登记一个稳定的死亡原因 id，然后在
 * <ul>
 *   <li>{@code lang/zh_cn.json} / {@code lang/en_us.json} 补上
 *       {@code death_reason.fangroles.<name>} 翻译；</li>
 *   <li>{@link FangRolesEventIds} 里补对应回放事件 id；</li>
 *   <li>{@code record/FangRolesReplayFormatters} 里注册格式化器；</li>
 *   <li>击杀逻辑处通过 Wathe {@code DeathApi} 的上下文带上这个原因。</li>
 * </ul>
 * 四件事要一起做，否则回放里会出现没有翻译的原始 key。</p>
 *
 * <h2>新增范例</h2>
 * <pre>{@code
 * public static final Identifier DRAGGED_INTO_DARKNESS_ID = FangRolesCore.id("dragged_into_darkness");
 * }</pre>
 *
 * <p>本类当前为空，等待第一个职业。</p>
 */
public final class FangRolesDeathReasons {

    // ---------------------------------------------------------------------
    // 死亡原因 id 定义区（当前为空）
    // ---------------------------------------------------------------------

    private FangRolesDeathReasons() {
    }
}
