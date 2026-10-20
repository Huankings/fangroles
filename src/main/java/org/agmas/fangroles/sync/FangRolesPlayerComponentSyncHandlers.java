package org.agmas.fangroles.sync;

import dev.doctor4t.wathe.api.sync.PlayerComponentSyncApi;
import net.minecraft.server.network.ServerPlayerEntity;
import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.fangroles.roles.poisoner.PoisonerComponentSyncHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * fangroles 接入 Wathe 私有玩家组件“额外观察者同步”API 的分类注册入口。
 *
 * <h2>为什么需要这个 API</h2>
 * <p>Wathe 的毒药、心情、体力、商店、便签、手雷投掷模式和本能组件默认只同步给本人，
 * 以避免一个玩家的连续状态被广播给附近所有客户端。但某些职业需要在客户端观察目标的真实状态，
 * 例如医师根据目标中毒层数绘制本能透视。此时必须注册
 * {@link PlayerComponentSyncApi.ObserverRule}，不能把组件改回全员同步，也不能为了“方便读取”
 * 新增逐 tick 广播。服务端能读取目标组件，并不代表观察者客户端也拥有最新值。</p>
 *
 * <h2>注册规则</h2>
 * <ul>
 *   <li>只在通用初始化里注册，不能只在客户端入口注册；规则在服务端执行。</li>
 *   <li>规则只能“额外允许”收件人，组件所属玩家本人始终允许；返回 {@code true} 即放行，
 *       {@code false} 继续询问低优先级。</li>
 *   <li>规则判断必须使用服务端真实职业 / 词条 / 组件状态，不能读取客户端本能按键或
 *       {@code MinecraftClient}。</li>
 *   <li>当前只支持 Wathe 的 {@code poison}、{@code mood}、{@code stamina}、{@code shop}、
 *       {@code note}、{@code grenade_throw_mode}、{@code instinct} 七个组件。
 *       fangroles 自定义组件不能注册到这里，否则会抛异常；自定义组件应自行设计最小显示状态。</li>
 *   <li>授权的是整个组件默认 NBT，不是字段级过滤。若只想暴露一个布尔标记，必须另建最小状态，
 *       不能把完整组件当作“只读一个字段”来用。</li>
 * </ul>
 *
 * <h2>刷新与清理</h2>
 * <p>Wathe 每 {@code GameConstants.COMPONENT_OBSERVER_REFRESH_INTERVAL_TICKS}（默认 20 tick）
 * 检查一次授权关系：新增权限时补发，失去权限时发送一次空 NBT 清空客户端缓存，权限未变化时不发包。
 * 因此不会因为本 API 产生逐 tick 广播。局中转职等“观察者权限变化但目标组件本身没变化”的场景，
 * 可在职业分配处理结束后调用 {@link #syncTrackedTargetsAfterRoleAssigned(ServerPlayerEntity)}，
 * 由各职业 handler 注册的刷新回调统一补发当前追踪目标。</p>
 *
 * <p>网络观察缓存不是玩法状态，不写存档、不参加时停快照。回溯改变职业后应按当前真实身份
 * 重新判定权限，而不是恢复旧网络连接关系。</p>
 *
 * <h2>本类只做编排</h2>
 * <p>具体规则放 {@code roles/<role_id>/<RoleName>ComponentSyncHandler} 或
 * {@code modifiers/<modifier_id>/<ModifierName>ComponentSyncHandler}，本类只调用它们的
 * {@code init()}。Wathe 原生本能心情规则已由 {@code InstinctMoodSyncHandler} 注册，
 * fangroles 不要重复注册同一 {@code PlayerMoodComponent} 规则。</p>
 */
public final class FangRolesPlayerComponentSyncHandlers {
    private static boolean initialized;

    /** 各职业 handler 注册的“转职后补发当前追踪目标”回调。 */
    private static final List<Consumer<ServerPlayerEntity>> ROLE_ASSIGNED_REFRESHERS = new ArrayList<>();

    private FangRolesPlayerComponentSyncHandlers() {
    }

    /**
     * 注册各职业 / 词条自己的观察者规则。
     *
     * <p>新增职业时在这里调用对应所有者的 {@code *ComponentSyncHandler.init()}，
     * 不要把具体职业判断写进本聚合类。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // 毒师需要观察目标的中毒状态来做被动透视。
        PoisonerComponentSyncHandler.init();
        FangRolesCore.LOGGER.debug("[fangroles] 私有组件观察者同步 API 接入完成");
    }

    /**
     * 注册一个“职业分配后补发当前追踪目标”的回调。
     *
     * <p>职业 handler 在自己的 {@code init()} 里调用本方法，例如：
     * {@code registerRoleAssignedRefresh(player -> PlayerComponentSyncApi.syncTrackedToRecipient(KEY, player))}。
     * 这样聚合类不需要认识具体组件，也不会把职业判断堆到一个大类里。</p>
     */
    public static void registerRoleAssignedRefresh(Consumer<ServerPlayerEntity> refresher) {
        ROLE_ASSIGNED_REFRESHERS.add(Objects.requireNonNull(refresher, "refresher"));
    }

    /**
     * 在职业 / 词条分配完成后补发当前追踪目标。
     *
     * <p>正常开局时目标通常还没有状态变化；这个入口主要覆盖局中 {@code /setRole}、状态转职等
     * “观察者刚获得能力，但目标组件没有刚好变化”的场景。Wathe 的周期检查会兜底，
     * 这里只是让新职业立即拿到数据，不必等待下一次 20 tick。</p>
     */
    public static void syncTrackedTargetsAfterRoleAssigned(ServerPlayerEntity player) {
        if (ROLE_ASSIGNED_REFRESHERS.isEmpty()) {
            return;
        }
        for (Consumer<ServerPlayerEntity> refresher : List.copyOf(ROLE_ASSIGNED_REFRESHERS)) {
            refresher.accept(player);
        }
    }
}
