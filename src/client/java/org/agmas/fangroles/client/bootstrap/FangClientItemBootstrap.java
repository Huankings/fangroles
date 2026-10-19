package org.agmas.fangroles.client.bootstrap;

import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端「物品」职责的注册入口。
 *
 * <p>本类负责物品在客户端的显示与交互注册，具体包含：</p>
 * <ol>
 *   <li><b>物品颜色</b>：{@code ItemColorProviderRegistry}（如果需要按 NBT 变色）。</li>
 *   <li><b>标准 Tooltip 与冷却读秒</b>：Wathe {@code ItemTooltipApi.registerItem(...)} /
 *       {@code registerItems(...)}。
 *       <b>不要</b>再注册自己的全局 {@code ItemTooltipCallback}。</li>
 *   <li><b>动态附加说明</b>：{@code ItemTooltipApi.registerAppender(...)}，
 *       只用于真正的动态文本（例如「当前目标：xxx」）。</li>
 *   <li><b>模型谓词</b>：{@code ModelPredicateProviderRegistry}。</li>
 *   <li><b>物品输入</b>：例如蓄力 / 直投模式切换等物品专属按键处理
 *       （放 {@code client/items/}，由本类调用）。</li>
 * </ol>
 *
 * <h2>冷却读秒的正确写法（很容易踩坑）</h2>
 * <p>读秒必须读取当前 {@code ItemCooldownManager.Entry} 的 {@code endTick - tick}。</p>
 * <p><b>禁止</b>用 {@code GameConstants.ITEM_COOLDOWNS * getCooldownProgress(...)} 反推，
 * 因为 Wathe 的 {@code GunShotApi} modifier、开局冷却和多来源冷却都会让实际冷却
 * 与基础常量不一致，反推会显示错误时间。</p>
 *
 * <h2>tooltip 的边界</h2>
 * <p>tooltip 只是客户端显示，<b>不得</b>作为服务端购买、攻击或能力合法性判断依据。
 * 服务端必须独立重新校验。</p>
 *
 * <h2>tooltip 翻译格式</h2>
 * <p>{@code .tooltip} 翻译允许使用 {@code \n} 分行；
 * 灰色描述、红色冷却文字和紧凑时间格式由 Wathe 统一处理，
 * 扩展不要自己拼颜色代码。</p>
 */
public final class FangClientItemBootstrap {
    private FangClientItemBootstrap() {
    }

    /**
     * 注册所有物品客户端相关内容。
     *
     * <p>新增物品时在这里补颜色 / Tooltip / 模型谓词 / 输入处理的注册调用。</p>
     */
    public static void register() {
        // 目前没有物品，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 客户端物品注册完成（当前为空）");
    }
}
