package org.agmas.fangroles.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 接入 Wathe 客户端输入 API 的聚合入口。
 *
 * <h2>统一入口，不再写输入 mixin</h2>
 * <p>{@code ClientInputApi} 统一调度 KeyBinding、原始键盘、鼠标按钮、鼠标滚轮、
 * 输入 tick 维护，以及攻击 / 使用 / 选取方块 / 持续挖掘最终动作。扩展不应再分别 mixin
 * {@code KeyBinding}、{@code Keyboard}、{@code Mouse}、{@code MinecraftClient#doAttack} /
 * {@code doItemUse} / {@code doItemPick} / {@code handleBlockBreaking}，否则多个扩展会争抢
 * mixin priority，并出现“能拦攻击却拦不住滚轮和短按队列”的状态分裂。</p>
 *
 * <h2>注册语义</h2>
 * <ul>
 *   <li>priority 越大越先执行；同 priority 下后注册者先执行；相同 id 再次注册会替换旧规则。</li>
 *   <li>{@code PASS} 继续询问低优先级；{@code ALLOW} 停止规则链并保留原版输入 / 动作；
 *       {@code BLOCK} 停止规则链并取消输入 / 动作。</li>
 *   <li>KeyBinding 规则可用 {@code KeyResult.override(...)} 覆盖读取结果；
 *       最终动作规则可用 {@code ActionResult.handled(...)} 表示扩展已经自行完成动作。</li>
 * </ul>
 *
 * <h2>按键队列清理</h2>
 * <p>只把读取结果改成 false 会让短按在状态结束后补执行。需要清短按时调用
 * {@code ClientInputApi.clearPressQueue(key)}；攻击、使用、背包、丢弃和选槽等离散动作
 * 通常调用 {@code releaseKey(key)} 同时清 pressed 与 timesPressed。
 * 特殊相机需要读取真实绑定物理状态时才用 {@code isPhysicallyPressed(...)}，
 * 不要自己 accessor {@code timesPressed} / {@code boundKey}。</p>
 *
 * <h2>客户端与服务端边界</h2>
 * <p>这些规则只负责本地输入体验和竞态兜底。攻击、物品使用、职业能力和自定义 C2S 包
 * 必须继续在服务端校验职业、存活、冷却、距离和目标权限，客户端 {@code BLOCK} 不能作为安全边界。</p>
 *
 * <p>本类只调用各职业 / 词条的 {@code *InputRules.register()}，状态判断和清理策略留在对应包内，
 * 不要重新堆成一个认识所有组件的大型输入类。</p>
 */
@Environment(EnvType.CLIENT)
public final class FangClientInputBootstrap {
    private static boolean registered;

    private FangClientInputBootstrap() {
    }

    /**
     * 注册所有职业 / 词条的客户端输入规则。
     *
     * <p>新增输入限制时在这里调用对应所有者的 {@code *InputRules.register()}。</p>
     */
    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        // 目前没有职业或词条需要输入规则，保留空入口。
        // 后续在这里调用各所有者的 *InputRules.register()。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端输入 API 接入完成（当前为空）");
    }
}
