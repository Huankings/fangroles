package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.fangroles.record.FangRolesReplayFormatters;
import org.agmas.fangroles.roles.poisoner.PoisonerReplayFormatters;

/**
 * fangroles 回放格式化器（Replay formatter）注册入口。
 *
 * <h2>为什么需要回放格式化器</h2>
 * <p>Wathe 的回放只保存事件 id 和结构化数据（UUID、数值、稳定 id），
 * 展示文案由格式化器在客户端本地翻译出来。
 * 所以新增一种职业事件时，必须同时补：
 * <ol>
 *   <li>事件 id（登记在 {@code registry/FangRolesEventIds}）</li>
 *   <li>写入回放数据的服务端代码（走 {@code GameRecordManager}）</li>
 *   <li><b>本类的格式化器</b></li>
 *   <li>{@code lang/zh_cn.json} 与 {@code lang/en_us.json} 的翻译</li>
 *   <li>回合结束 / 玩家重置时的清理</li>
 * </ol>
 * 缺任何一项都会导致回放里出现原始 key 或空白行。</p>
 *
 * <h2>新增格式化器范例</h2>
 * <pre>{@code
 * ReplayRegistry.registerFormatter(
 *         FangRolesEventIds.ABILITY_USED_ID,
 *         (world, event) -> {
 *             // 数据里只存稳定 id / UUID / 必要的显示名兜底，不要存中文句子
 *             UUID actor = event.getUuid("actor");
 *             String roleId = event.getString("role");
 *             // 展示文字一律用 Text.translatable(...) / Component.translatable(...)
 *             return Text.translatable("replay.fangroles.ability_used",
 *                     ReplayNames.resolvePlayerName(world, actor));
 *         });
 * }</pre>
 *
 * <h2>硬性约定</h2>
 * <ul>
 *   <li>回放数据里<b>不要</b>硬编码中文 / 英文字符串，只存稳定 id、UUID 和必要显示名兜底。</li>
 *   <li>玩家可能已经掉线，因此不能只靠「在线玩家表」查名字，要有兜底显示。</li>
 *   <li>展示文字统一用 {@code Text.translatable(...)}，翻译 key 前缀用 {@code replay.fangroles.}。</li>
 * </ul>
 */
public final class FangRolesReplayBootstrap {
    private FangRolesReplayBootstrap() {
    }

    /**
     * 注册所有回放格式化器。
     *
     * <p>新增职业时在这里调用该职业自己的 {@code *ReplayFormatters.register()}，
     * 或者直接在本类按职业分组书写。建议职业数超过 3 个后改为按职业拆分文件。</p>
     */
    public static void register() {
        PoisonerReplayFormatters.register();
        FangRolesReplayFormatters.register();
        FangRolesCore.LOGGER.debug("[fangroles] 回放格式化器注册完成");
    }
}
