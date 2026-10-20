package org.agmas.fangroles;

import net.minecraft.entity.player.PlayerEntity;
import org.agmas.fangroles.registry.FangRolesCore;
import org.agmas.fangroles.roles.poisoner.PoisonerGasPlayerComponent;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

/**
 * fangroles 的 CCA（Cardinal Components API）组件注册入口。
 *
 * <p>本类由 {@code fabric.mod.json} 的 {@code cardinal-components} entrypoint 加载，
 * <b>不</b>由 {@code FangRolesBootstrap} 调用。</p>
 *
 * <h2>什么时候需要 CCA 组件</h2>
 * <p>需要跨 tick、跨死亡、跨掉线、跨回合重置、或需要同步给客户端的状态，
 * 都应该建组件，而不是塞进静态 Map：</p>
 * <ul>
 *   <li><b>玩家状态</b>（冷却、计数、目标、标记）→ entity component</li>
 *   <li><b>世界共享状态</b>（全局阶段、配对表、全场效果）→ world component</li>
 *   <li><b>跨维度 / 全服唯一状态</b> → scoreboard component</li>
 * </ul>
 *
 * <h2>新增组件的三步</h2>
 * <ol>
 *   <li>在 {@code roles/<role_id>/} 下建组件类，写好
 *       {@code ComponentKey}、字段、{@code sync()}、{@code reset()}、
 *       {@code writeToNbt} / {@code readFromNbt}。</li>
 *   <li>在本类对应的 {@code register*} 方法里加一行注册
 *       （玩家组件用 {@code registry.beginRegistration(...).respawnStrategy(...).end(...)}）。</li>
 *   <li>在 {@code src/main/resources/fabric.mod.json} 的
 *       {@code custom.cardinal-components} 数组里补上
 *       {@code "fangroles:<组件路径>"}，否则 CCA 不会分配该组件。</li>
 * </ol>
 *
 * <h2>同步范围必须收窄（网络带宽硬约束）</h2>
 * <p>默认实现 {@code shouldSyncWith}，只在确实需要他人观察时才扩大收件人范围。
 * 只给本人用的冷却 / 位置 / 状态组件<b>必须</b>限制为本人，
 * 否则 20 人以上挂机时会产生持续的高发送带宽。
 * 详见 {@code AGENTS.md} 的「网络带宽与同步规范」。</p>
 *
 * <h2>时停回溯注册（跨模组协作注意）</h2>
 * <p>如果本模组将来与 NoellesRoles 的时停者机制共处同一环境，
 * 新增的运行态玩家组件需要评估是否进入回溯快照。
 * 内置清单由 NoellesRoles 维护，外部扩展应通过
 * {@code TimekeeperSnapshotApi.registerPlayerComponent(...)} /
 * {@code registerWorldComponent(...)} 注册，
 * <b>不要</b>直接修改它的内部清单。</p>
 */
public class FangRolesComponents implements EntityComponentInitializer, WorldComponentInitializer {

    public FangRolesComponents() {
    }

    /**
     * 注册玩家（entity）组件。
     *
     * <p>新增范例（写在 {@code roles/<role_id>/<RoleName>PlayerComponent} 里）：</p>
     * <pre>{@code
     * registry.beginRegistration(PlayerEntity.class, VanisherPlayerComponent.KEY)
     *         .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)   // 局内运行态，复活/下一局不复制
     *         .end(VanisherPlayerComponent::new);
     * }</pre>
     * <p>{@code RespawnCopyStrategy} 选择原则：
     * 局内运行态（冷却、计数、标记）一律 {@code NEVER_COPY}；
     * 只有跨局保留的进度才考虑 {@code ALWAYS_COPY}，但那类数据通常应该放配置组件。</p>
     */
    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        /*
         * 毒气状态是局内瞬时状态，复活 / 下一局不能复制；只同步给本人，
         * 因为客户端只需要用它阻止自己疾跑。
         */
        registry.beginRegistration(PlayerEntity.class, PoisonerGasPlayerComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
                .end(PoisonerGasPlayerComponent::new);
        FangRolesCore.LOGGER.debug("[fangroles] 玩家 CCA 组件注册完成");
    }

    /**
     * 注册世界（world）组件。
     *
     * <p>新增范例：{@code worldComponentFactoryRegistry.register(SomeWorldComponent.KEY, SomeWorldComponent::new);}</p>
     * <p><b>注意</b>：世界组件<b>不要</b>在 {@code ServerTickingComponent#serverTick} 里
     * 无条件调用 {@code KEY.sync(world)}。配置、词条表、配对表和全局标记
     * 必须在值变化后批量同步一次。</p>
     */
    @Override
    public void registerWorldComponentFactories(@NotNull WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
        // 目前没有世界组件，空实现。
        FangRolesCore.LOGGER.debug("[fangroles] 世界 CCA 组件注册完成（当前为空）");
    }
}
