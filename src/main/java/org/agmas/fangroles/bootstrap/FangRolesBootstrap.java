package org.agmas.fangroles.bootstrap;

import org.agmas.fangroles.FangRolesComponents;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.FangRolesParticles;
import org.agmas.fangroles.FangRolesSounds;
import org.agmas.fangroles.ModItems;
import org.agmas.fangroles.appearance.FangRolesAppearanceHandlers;
import org.agmas.fangroles.bed.FangRolesBedEffects;
import org.agmas.fangroles.collision.FangRolesPlayerCollisionHandlers;
import org.agmas.fangroles.combat.FangRolesCombatBootstrap;
import org.agmas.fangroles.death.FangRolesDeathBootstrap;
import org.agmas.fangroles.pickup.FangRolesItemPickupHandlers;
import org.agmas.fangroles.record.FangRolesReplayFormatters;
import org.agmas.fangroles.reset.FangRolesResetBootstrap;
import org.agmas.fangroles.registry.FangRolesRoleGroups;
import org.agmas.fangroles.registry.FangRolesModifierRegistry;
import org.agmas.fangroles.registry.FangRolesRoleRegistry;
import org.agmas.fangroles.roleassign.FangRolesRoleAssignedBootstrap;
import org.agmas.fangroles.roles.poisoner.PoisonerTimekeeperHandler;
import org.agmas.fangroles.shop.FangRolesShopBootstrap;
import org.agmas.fangroles.sync.FangRolesPlayerComponentSyncHandlers;
import org.agmas.fangroles.tray.FangRolesTrayEffects;
import org.agmas.fangroles.victory.FangRolesVictoryRules;
import org.agmas.fangroles.visibility.FangRolesTargetVisibilityHandlers;

/**
 * fangroles 的总启动编排器。
 *
 * <p>主入口 {@code Fangroles} 只调用这里的 {@link #init()}，
 * <b>所有初始化顺序都集中在本类维护</b>。这样做的原因：
 * 职业注册、事件监听、网络包、商店、回放之间存在隐含顺序依赖，
 * 一旦散落到多个类里就会出现「本地跑得通、换个入口顺序就炸」的问题。</p>
 *
 * <h2>顺序设计原则（后续新增初始化器时请遵守）</h2>
 * <ol>
 *   <li><b>先基础、后业务</b>：分组与配置 → 注册表 → 物品 / 实体 / 粒子 / 声音。</li>
 *   <li><b>职业实例要早于一切引用职业的地方</b>：{@link FangRolesRoleRegistry#init()}
 *       必须在 {@link FangRolesRoleGroups#init()}、角色上限、商店、胜利规则之前调用。</li>
 *   <li><b>Wathe / Harpy 的 API 接入放中间</b>：碰撞、移动、经济、心情任务、疯魔、停电、可见性。
 *       这些是「往上游注册规则」，本身不创建本模组的资源。</li>
 *   <li><b>分配相关（Harpy assignment）放后面</b>：角色上限与分配规则要在职业和词条都注册完之后。</li>
 *   <li><b>网络包注册最后</b>：payload codec 必须在任何发送 / 接收代码之前注册，
 *       而发送代码通常在职业逻辑里，所以统一压到末尾。</li>
 *   <li><b>事件监听收尾</b>：{@link FangRolesEventBootstrap#init()} 会挂上 server tick
 *       和回合清理，必须等所有需要被清理的状态都注册好之后再挂。</li>
 * </ol>
 *
 * <p><b>幂等保护</b>：{@code initialized} 标记防止重复初始化。
 * 某些调试场景（例如热重载或测试环境）可能重复触发入口，重复注册会抛异常。</p>
 */
public final class FangRolesBootstrap {
    private static boolean initialized = false;

    private FangRolesBootstrap() {
    }

    /**
     * fangroles 的唯一初始化入口。
     *
     * <p>新增任何一个初始化器时，请在本方法里按上面的顺序原则找到合适位置插入一行调用，
     * 并在该行上方写一句中文注释说明「为什么放在这里」。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // ---- 1. 基础分组与注册表 ----
        // 分组必须先于职业实例之后的任何引用；注册表本身负责触发 Role 静态字段注册。
        FangRolesRoleRegistry.init();
        FangRolesRoleGroups.init();
        FangRolesModifierRegistry.init();

        // ---- 2. 本模组自有资源：物品 / 实体 / 粒子 / 声音 ----
        // 这些是纯注册行为，不依赖上游 API 的运行态，所以放前面。
        FangRolesSounds.initialize();
        ModItems.init();
        FangRolesEntities.init();
        FangRolesParticles.init();

        // ---- 3. 上游效果表接入：托盘 / 床 / 尸体外观 ----
        FangRolesTrayEffects.register();
        FangRolesBedEffects.register();
        FangRolesAppearanceHandlers.register();

        // ---- 4. 玩家 / 目标可见性、碰撞与掉落物拾取 ----
        // 这三项都会影响「谁能拿什么、谁能穿过谁」，属于基础玩法规则，要早于职业能力注册。
        FangRolesTargetVisibilityHandlers.init();
        FangRolesPlayerCollisionHandlers.init();
        FangRolesItemPickupHandlers.init();     // 普通掉落物拾取（PASS / ALLOW / DENY）
        FangRolesPlayerComponentSyncHandlers.init(); // 私有组件选择性观察者同步

        // 毒气云/毒气弹是自定义实体，毒气状态是自定义组件；必须在时停者首张历史快照前注册。
        PoisonerTimekeeperHandler.init();

        // ---- 5. Wathe 公开 API 接入（按 API 领域拆分，聚合类只调用各职业 handler）----
        FangRolesMovementBootstrap.init();     // 体力（含免消耗规则）与移动速度修正
        FangRolesReplayBootstrap.register();   // 回放格式化器
        FangRolesPsychoBootstrap.init();       // 疯魔模式
        FangRolesMoodTypeBootstrap.init();     // 动态心情类型（REAL / FAKE / NONE）
        FangRolesRoundEndBootstrap.init();     // 结算阵营迁移
        FangRolesMoodTaskBootstrap.init();     // 心情任务
        FangRolesBlackoutBootstrap.init();     // 停电机制
        FangRolesCombatBootstrap.init();       // 枪击 / 左轮 / 匕首冷却 / 武器目标
        FangRolesDeathBootstrap.init();        // 击杀与死亡分阶段流程
        FangRolesVictoryRules.init();          // 特殊胜利规则

        // ---- 6. Harpy 分配接入：上限 + 分配规则 + 分配后初始化 ----
        // 必须在职业与词条都注册完成之后，否则上限会落到不存在的职业上。
        FangRolesLimitsBootstrap.initStaticLimits();
        FangRolesRoleAssignedBootstrap.init();

        // ---- 7. 商店 / 经济 ----
        // 商店条目会引用物品和职业，所以压在职业注册之后。
        FangRolesShopBootstrap.init();
        FangRolesEconomyBootstrap.init();

        // ---- 8. 自定义网络包 codec ----
        // 放最后，保证任何发送 / 接收代码执行时 codec 一定已经注册。
        FangRolesPayloadTypes.register();

        // ---- 9. 玩家 / 回合重置与生命周期事件（收尾）----
        // 重置规则需要认识所有职业组件和世界状态，所以放在业务注册之后；
        // 事件监听再最后挂，确保被清理的静态缓存、延迟任务都已注册完成。
        FangRolesResetBootstrap.init();
        FangRolesEventBootstrap.init();
        FangRolesPacketReceivers.register();
    }
}
