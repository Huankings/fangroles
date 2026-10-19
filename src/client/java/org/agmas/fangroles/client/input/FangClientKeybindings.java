package org.agmas.fangroles.client.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.noellesroles.client.input.NoellesClientKeybindings;
import org.lwjgl.glfw.GLFW;

/**
 * fangroles 公共能力键的读取入口。
 *
 * <h2>默认策略：直接复用 NoellesRoles 的能力键，不额外注册新按键</h2>
 * <p>本模组与 NoellesRoles 处在同一套玩法环境里，两者用的是同一个「能力」语义。
 * 因此 fangroles <b>默认不注册自己的按键</b>，而是直接读取 NoellesRoles 的
 * {@link NoellesClientKeybindings#getAbilityBind()}。</p>
 *
 * <p>这样做有三个好处：</p>
 * <ol>
 *   <li><b>不会出现两个「能力」键</b>：玩家只需要按一次默认的 <b>G</b> 键，
 *       不会因为装了 fangroles 就在按键设置里多出一条重复项。</li>
 *   <li><b>玩家改键自动跟随</b>：玩家在 NoellesRoles 那条按键上把 G 改成别的键后，
 *       fangroles 读到的是同一个 {@link KeyBinding} 实例，自动同步，无需两边各改一次。</li>
 *   <li><b>与上游语义一致</b>：NoellesRoles 的能力键分类是
 *       {@code category.wathe.keybinds}、翻译 key 是 {@code key.noellesroles.ability}，
 *       显示名和分类天然统一，不会出现「同类功能散落在两个分类里」的观感问题。</li>
 * </ol>
 *
 * <h2>为什么用「懒解析」而不是在 register() 里缓存</h2>
 * <p>{@code NoellesClientKeybindings.getAbilityBind()} 返回的是它的<b>静态字段</b>，
 * 由 NoellesRoles 自己的客户端入口 {@code NoellesrolesClient.onInitializeClient()}
 * 调用 {@code register()} 之后才有值。</p>
 * <p>Fabric 会按 {@code fabric.mod.json} 顺序加载各模组的客户端入口，但
 * <b>不保证</b> fangroles 一定排在 NoellesRoles 之后。如果在 fangroles 的
 * {@code onInitializeClient()} 里就把结果缓存下来，一旦加载顺序反了就会永久拿到
 * {@code null}，表现为「按键完全没反应」而且极难排查。</p>
 * <p>所以本类的每个方法都在<b>被调用的那一刻</b>现读一次上游字段：
 * 所有实际调用都发生在游戏运行中（tick、HUD 渲染），此时两个模组的入口早已执行完毕。
 * 这样既不依赖加载顺序，也能自动跟随上游在重载场景下的重新赋值。</p>
 *
 * <h2>逃生通道：确实需要独立按键时</h2>
 * <p>如果后续某个职业的能力必须与 NoellesRoles 的 G 键分开
 * （例如需要「按住蓄力」而 G 键已被占用、或需要独立的第二能力键），
 * 调用一次 {@link #registerFallbackAbilityBind()} 即可。
 * <b>该方法默认不会被调用</b>，因此当前不会产生任何多余的按键项。</p>
 */
public final class FangClientKeybindings {

    /**
     * fangroles 自己的备用能力键。
     *
     * <p><b>默认保持 {@code null}</b>，只有在显式调用
     * {@link #registerFallbackAbilityBind()} 之后才会被赋值。
     * 保留这个字段而不是直接删掉，是为了让「复用上游」和「将来独立注册」
     * 两条路径共用同一套读取方法，避免将来改需求时要改一堆调用点。</p>
     */
    private static KeyBinding fallbackAbilityBind;

    private FangClientKeybindings() {
    }

    /**
     * 客户端初始化入口。
     *
     * <p>当前<b>刻意不做任何注册</b>：能力键完全复用 NoellesRoles 的那一个。
     * 这个方法保留下来，是为了将来需要注册无关的附加按键时有个明确的落点，
     * 同时也让 {@code FangrolesClient} 的初始化编排保持完整、不必因为本次改动而删行。</p>
     */
    public static void register() {
        FangRolesCore.LOGGER.debug("[fangroles] 能力键复用 NoellesRoles（key.noellesroles.ability，默认 G 键）；fangroles 不额外注册按键");
    }

    /**
     * 注册 fangroles 自己的备用能力键（<b>默认不需要调用</b>）。
     *
     * <p>只有在「确实需要与 NoellesRoles 的 G 键分开」时才调用。
     * 注册后 {@link #getAbilityBind()} 会优先返回它，
     * 而 {@link #getNoellesAbilityBind()} 仍可读到上游那一个。</p>
     *
     * <p>调用时机提示：本方法必须在客户端初始化阶段调用（通常在
     * {@link #register()} 里），因为原版 {@code KeyBinding} 需要在那时登记才能
     * 出现在「选项 → 按键绑定」界面中。</p>
     */
    public static void registerFallbackAbilityBind() {
        if (fallbackAbilityBind != null) {
            // 幂等保护：重复注册会让按键设置里出现重复项。
            return;
        }
        fallbackAbilityBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fangroles.ability",
                InputUtil.Type.KEYSYM,
                // 默认仍然沿用 G：与 NoellesRoles 保持一致，玩家迁移到独立按键时不需要重新适应。
                GLFW.GLFW_KEY_G,
                // 分类沿用 Wathe 的按键分类，避免同类功能散落在两个分类里。
                "category.wathe.keybinds"
        ));
        FangRolesCore.LOGGER.info("[fangroles] 已注册 fangroles 独立能力键（key.fangroles.ability）");
    }

    /**
     * 读取 NoellesRoles 的能力键实例，可能为 {@code null}。
     *
     * <p>这是<b>上游原始对象</b>，用于需要在 HUD 上显示「按 X 使用能力」时读取绑定键名，
     * 或需要把该键纳入输入锁（被控制 / 眩晕期间屏蔽）逻辑时做 {@code equals} 比较。</p>
     */
    public static KeyBinding getNoellesAbilityBind() {
        // 每次现读，不缓存：详见类注释里关于加载顺序的说明。
        return NoellesClientKeybindings.getAbilityBind();
    }

    /**
     * 读取 fangroles 应当使用的能力键。
     *
     * <p>解析优先级：</p>
     * <ol>
     *   <li>fangroles 自己的备用键（仅在显式调用
     *       {@link #registerFallbackAbilityBind()} 之后存在）</li>
     *   <li>NoellesRoles 的能力键（<b>默认走这一条</b>）</li>
     * </ol>
     *
     * <p>返回 {@code null} 表示两个来源都还不可用（例如客户端尚未初始化完毕）。
     * <b>所有调用方都必须做 null 判断</b>，不要直接链式调用
     * {@code getAbilityBind().wasPressed()}。</p>
     */
    public static KeyBinding getAbilityBind() {
        if (fallbackAbilityBind != null) {
            return fallbackAbilityBind;
        }
        return getNoellesAbilityBind();
    }

    /**
     * 能力键是否在本 tick 被按下（<b>边沿触发</b>，一次按下只返回一次 true）。
     *
     * <p>没有可用按键时返回 {@code false}，调用方不需要额外判空。</p>
     *
     * <h3>调用注意</h3>
     * <p>原版 {@code wasPressed()} 会消费这次的按下事件：同一个 tick 内
     * 多个地方调用，只有第一个能拿到 {@code true}。
     * 因此 fangroles 侧应<b>只在唯一的能力分发器</b>里读一次，再分发到各职业，
     * 不要在多个职业的 handler 里各读一次。</p>
     */
    public static boolean wasAbilityPressed() {
        KeyBinding bind = getAbilityBind();
        return bind != null && bind.wasPressed();
    }

    /**
     * 能力键当前是否被按住（<b>电平触发</b>，按住期间每 tick 都是 true）。
     *
     * <p>适用于「按住蓄力 / 按住持续生效」这类设计。
     * <b>不要</b>用它做「按一次触发一次」的能力，那会变成按住期间每 tick 触发。
     * 没有可用按键时返回 {@code false}。</p>
     */
    public static boolean isAbilityPressed() {
        KeyBinding bind = getAbilityBind();
        return bind != null && bind.isPressed();
    }

    /**
     * 在「选项 → 按键绑定」界面里展示的按键名（例如 {@code G}）。
     *
     * <p>HUD 上的「按 X 使用能力」提示必须走这里，
     * 这样玩家改键后提示会自动跟着变。</p>
     *
     * <p>没有可用按键时返回 {@code "???"} 兜底，
     * 避免 HUD 出现空白或者 {@code null} 文本。</p>
     */
    public static String getAbilityBindText() {
        KeyBinding bind = getAbilityBind();
        if (bind == null) {
            return "???";
        }
        return bind.getBoundKeyLocalizedText().getString();
    }

    /**
     * 指定按键是否就是当前的能力键。
     *
     * <p>给输入锁（被控制、眩晕、绑架等需要屏蔽玩家操作的场景）使用：
     * 这些逻辑需要判断「被按下的这个键是不是能力键」，从而决定要不要拦截。
     * 直接传原版 {@code KeyBinding} 进来比较，避免调用方自己写
     * {@code getAbilityBind() == null || ...} 这套重复判空。</p>
     */
    public static boolean matchesAbilityBind(KeyBinding key) {
        KeyBinding bind = getAbilityBind();
        return bind != null && bind.equals(key);
    }
}
