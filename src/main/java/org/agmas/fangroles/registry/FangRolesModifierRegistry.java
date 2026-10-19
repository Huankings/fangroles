package org.agmas.fangroles.registry;

import org.agmas.harpymodloader.modifiers.Modifier;

/**
 * fangroles 的 HarpyModLoader 词条（{@link Modifier}）注册表。
 *
 * <p>词条与职业的区别：词条可以随机挂到任意玩家身上（除非你用职业名单限制），
 * 显示在旁观 / 创造模式的玩家名牌上，但<b>不会</b>出现在本能透视里。</p>
 *
 * <h2>Harpy {@code Modifier} 六参构造</h2>
 * <ol>
 *   <li>{@code Identifier identifier}：词条 id，用 {@link FangRolesCore#id(String)} 生成</li>
 *   <li>{@code int color}：词条色（{@code 0xRRGGBB}）</li>
 *   <li>{@code ArrayList<Role> cannotBeAppliedTo}：禁止出现的职业名单，可为 {@code null}</li>
 *   <li>{@code ArrayList<Role> canOnlyBeAppliedTo}：仅允许出现的职业名单，可为 {@code null}</li>
 *   <li>{@code boolean killerOnly}：是否只给杀手能力玩家</li>
 *   <li>{@code boolean civilianOnly}：是否只给非杀手能力玩家</li>
 * </ol>
 *
 * <h2>新增词条范例（照抄改名字即可）</h2>
 * <pre>{@code
 * // FangRolesIds.java 里登记 id
 * public static final Identifier FORGETFUL_ID = FangRolesCore.id("forgetful");
 *
 * // 本类里注册
 * public static final Modifier FORGETFUL = HMLModifiers.registerModifier(new Modifier(
 *         FangRolesIds.FORGETFUL_ID,
 *         0xFFD38A,
 *         null,                 // 不排除任何职业
 *         null,                 // 不限制任何职业
 *         false,                // 不限杀手
 *         false                 // 不限平民
 * ));
 *
 * // lang/zh_cn.json 与 lang/en_us.json 各补一条：
 * //   "announcement.modifier.fangroles.forgetful": "健忘"
 *
 * // 需要更细的条件（例如“只有本模组注册了某职业时才可能出现”）时，
 * // 不要扩写静态职业名单，改用 setEligibilityPredicate(...) 做动态判断。
 * }</pre>
 *
 * <p><b>重要</b>：不要用 {@code Set.of(...)} 之类的集合去装职业名单。
 * {@code Set.of(...)} 遇到重复元素会在启动时直接抛 {@code IllegalArgumentException: duplicate element}，
 * 导致服务器起不来。需要去重请用 {@code new LinkedHashSet<>(List.of(...))}。</p>
 *
 * <p>本类当前刻意不注册任何词条：模板阶段注册空词条会让 Harpy 的分配日志出现
 * 无翻译、无规则的词条，反而不利于后续排查。</p>
 */
public final class FangRolesModifierRegistry {

    // ---------------------------------------------------------------------
    // 词条实例定义区（当前为空）
    // ---------------------------------------------------------------------

    private FangRolesModifierRegistry() {
    }

    /**
     * 显式初始化入口：触发类加载，让上面的 {@code static final} 词条完成注册。
     *
     * <p>后续如果需要在词条注册后补 {@code setEligibilityPredicate(...)}
     * 或把词条加入某些跨系统分组，也统一写在这里。</p>
     */
    public static void init() {
        // 目前没有词条，空实现。请勿删除本类与 FangRolesBootstrap 中的调用。
    }
}
