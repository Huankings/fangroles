package org.agmas.fangroles.client;

import net.fabricmc.api.ClientModInitializer;
import org.agmas.fangroles.client.appearance.FangAppearanceHandlers;
import org.agmas.fangroles.client.bootstrap.FangClientConnectionBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientEntityRendererBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientItemBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientMovementBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientNetworkingBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientParticleBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientPsychoBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientRoleBootstrap;
import org.agmas.fangroles.client.bootstrap.FangClientTickBootstrap;
import org.agmas.fangroles.client.crosshair.FangCrosshairHandlers;
import org.agmas.fangroles.client.hud.FangHudHandlers;
import org.agmas.fangroles.client.input.FangClientInputBootstrap;
import org.agmas.fangroles.client.input.FangClientKeybindings;
import org.agmas.fangroles.client.input.FangClientPreAttackHandlers;
import org.agmas.fangroles.client.instinct.FangInstinctHandlers;
import org.agmas.fangroles.client.inventory.FangInventoryButtons;
import org.agmas.fangroles.client.mood.FangPsychosisHandlers;
import org.agmas.fangroles.client.visibility.FangHeldItemVisibilityHandlers;

/**
 * fangroles 的 Fabric 客户端主入口。
 *
 * <p>本类<b>只负责调用各职责模块的初始化方法</b>，不承载任何状态、注册细节或渲染逻辑。</p>
 *
 * <h2>为什么要这样拆</h2>
 * <p>NoellesRoles 早期把按键、Tooltip、model predicate、实体渲染、网络包
 * 全部堆在客户端主入口里，导致该类超过 300 行、初始化顺序不可读，
 * 而且改一处要重新理解全部。本模板从一开始就按职责拆分，
 * 新增功能时先判断归属，再放进对应包：</p>
 *
 * <ul>
 *   <li>职业状态、能力输入、音乐、相机、职业专属效果 →
 *       {@code client/roles/<role_id>/}</li>
 *   <li>词条客户端状态与 UI → {@code client/modifiers/<modifier_id>/}
 *       或 {@code client/hud/modifiers/<modifier_id>/}</li>
 *   <li>通用按键、Wathe 客户端输入 / 最终动作规则、左键预攻击、能力键分发 →
 *       {@code client/input/}（输入规则统一走 {@code ClientInputApi}，不要再 mixin 原版输入类）</li>
 *   <li>物品颜色、Tooltip、model predicate、物品输入 → {@code client/items/}
 *       （注册清单由 {@link FangClientItemBootstrap} 编排）</li>
 *   <li>客户端 tick、网络包、连接清理、粒子、实体渲染、疯魔背景音 →
 *       {@code client/bootstrap/} 下对应的职责类</li>
 *   <li>皮肤、伪装、随机映射缓存 → {@code client/appearance/}</li>
 * </ul>
 *
 * <h2>初始化顺序说明</h2>
 * <p>先注册 API handler（本能、外观、HUD、准心、移动、手持隐藏、幻觉、背包按钮），
 * 它们只往 Wathe 的注册表里写规则，不依赖运行态；
 * 然后再做需要读配置 / 需要注册 codec 的 bootstrap（职业客户端、
 * 疯魔客户端、粒子、物品、网络、连接清理、tick、实体渲染、预攻击）。</p>
 */
public final class FangrolesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // ---- 1. API handler 注册（写规则，不依赖运行态）----
        FangClientKeybindings.register();          // 能力键：默认直接复用 NoellesRoles 的 G 键，不额外注册
        FangClientInputBootstrap.register();       // Wathe ClientInputApi：键位 / 鼠标 / 最终动作规则
        FangInstinctHandlers.register();           // 本能透视
        FangAppearanceHandlers.register();         // 玩家 / 尸体外观
        FangHudHandlers.register();                // 普通屏幕 HUD
        FangCrosshairHandlers.register();          // 准心图标 / 小进度条
        FangClientMovementBootstrap.init();        // 客户端移动表现
        FangHeldItemVisibilityHandlers.register(); // 手持物隐藏
        FangPsychosisHandlers.register();          // 低心情幻觉手持物
        FangInventoryButtons.register();           // 背包按钮与分页

        // ---- 2. 职责 bootstrap（注册 codec / 渲染器 / receiver 等）----
        FangClientRoleBootstrap.register();            // 职业客户端初始化聚合
        FangClientPsychoBootstrap.register();          // 疯魔皮肤与背景音乐
        FangClientParticleBootstrap.register();        // 粒子工厂
        FangClientItemBootstrap.register();            // 物品颜色 / Tooltip / 模型谓词
        FangClientNetworkingBootstrap.register();      // S2C receiver
        FangClientConnectionBootstrap.register();      // JOIN / DISCONNECT 清理
        FangClientTickBootstrap.register();            // 客户端每 tick 调度
        FangClientEntityRendererBootstrap.register();  // 实体渲染器
        FangClientPreAttackHandlers.register();        // 左键预攻击接管
    }
}
