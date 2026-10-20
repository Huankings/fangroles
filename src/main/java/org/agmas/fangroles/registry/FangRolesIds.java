package org.agmas.fangroles.registry;

import net.minecraft.util.Identifier;

/**
 * fangroles 所有职业和词条的稳定 id（注册名）登记处。
 *
 * <p>这里<b>只保存注册名</b>，不负责创建 {@code Role} / {@code Modifier} 实例。
 * 这样拆分的意义是：新增职业时可以先在这里登记 id，
 * 再去 {@link FangRolesRoleRegistry} 决定阵营和属性，
 * 而事件、HUD、回放、商店等其它系统都只引用这里的常量，不会各自重复写字符串。</p>
 *
 * <p><b>命名规范</b>：常量名用大写下划线，路径用小写下划线，两者对应，例如
 * {@code MY_ROLE_ID = FangRolesCore.id("my_role")}。</p>
 *
 * <h2>如何新增一个职业 id</h2>
 * <pre>{@code
 * // 1) 在这里登记稳定 id
 * public static final Identifier VANISHER_ID = FangRolesCore.id("vanisher");
 *
 * // 2) 去 FangRolesRoleRegistry 里创建 Role 实例并显式登记阵营
 * // 3) 去 bootstrap/FangRolesLimitsBootstrap 里设置最大生成数
 * // 4) 在 roles/vanisher/ 下新建职业包
 * }</pre>
 *
 * <p>当前已登记毒师。下面的示例保持注释，避免误注册一个没有实现和翻译的占位职业。</p>
 */
public final class FangRolesIds {

    // ---------------------------------------------------------------------
    // 职业 id
    // ---------------------------------------------------------------------
    /** 毒师：杀手阵营，使用毒针 / 催化剂 / 毒气弹。 */
    public static final Identifier POISONER_ID = FangRolesCore.id("poisoner");

    // ---------------------------------------------------------------------
    // 示例（保持注释，避免进入 Harpy 分配池）
    // ---------------------------------------------------------------------
    // public static final Identifier EXAMPLE_ID = FangRolesCore.id("example");

    private FangRolesIds() {
    }
}
