package org.agmas.fangroles.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.fangroles.registry.FangRolesCore;
import org.jetbrains.annotations.Nullable;

/**
 * fangroles 通用 HUD 辅助工具。
 *
 * <p>把「判断本地玩家是不是某职业且真实存活」「取玩家名兜底」「右下角 HUD 通用绘制」
 * 这些每个职业 HUD 都要写一遍的东西收敛在这里，
 * 避免各职业 HUD 各写一套造成行为不一致。</p>
 *
 * <h2>为什么需要存活过滤</h2>
 * <p>Wathe 的「存活」不是简单的 {@code player.isAlive()}：
 * 局内死亡会切成旁观，但旁观者仍然 {@code isAlive() == true}。
 * 所以职业 HUD 必须用 Wathe 的存活定义（{@code GameFunctions.isPlayerAliveAndSurvival(...)}），
 * 否则死亡后 HUD 会继续显示。</p>
 *
 * <h2>使用约定</h2>
 * <ul>
 *   <li>本类只提供<b>无状态</b>的辅助方法，不要在这里存职业状态。</li>
 *   <li>需要读服务端同步状态时，从对应的 CCA 组件读，
 *       不要在本类里做缓存（缓存放各职业的 {@code client/roles/} 包里）。</li>
 * </ul>
 */
public final class FangHudSupport {
    private FangHudSupport() {
    }

    /**
     * 取当前客户端玩家，可能为 {@code null}（主菜单 / 断线瞬间）。
     *
     * <p>所有 HUD provider 都应以本方法开头，并做好 {@code null} 判断。</p>
     */
    @Nullable
    public static PlayerEntity localPlayer() {
        return MinecraftClient.getInstance().player;
    }

    /**
     * 取玩家名，找不到时返回稳定兜底文本。
     *
     * <p>HUD 里显示玩家名必须走这里：玩家可能已经掉线或被卸载，
     * 直接 {@code player.getName()} 会拿到 {@code null} 或者显示成「未知」，
     * 用统一兜底可以保证不同 HUD 的降级文案一致。</p>
     */
    public static Text resolvePlayerName(@Nullable PlayerEntity player) {
        if (player == null) {
            return Text.translatable("hud.fangroles.unknown_player");
        }
        return player.getDisplayName();
    }

    /**
     * 调试日志统一出口。
     *
     * <p>HUD 相关调试日志量大，务必只在真正需要排查时调用，
     * 不要放在每帧渲染路径里无条件打印。</p>
     */
    public static void debug(String message) {
        FangRolesCore.LOGGER.debug("[fangroles][hud] {}", message);
    }

    // 后续可继续在此补充：
    //   - 右下角 HUD 的行距常量与起始 Y 坐标计算
    //   - 「本地玩家是某职业且真实存活」的统一判断
    //   - 倒计时文本的紧凑格式（与 Wathe ItemTooltipApi 保持一致的分秒格式）
}
