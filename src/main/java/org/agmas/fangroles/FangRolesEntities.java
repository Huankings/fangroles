package org.agmas.fangroles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.agmas.fangroles.entity.PoisonGasBombEntity;
import org.agmas.fangroles.entity.PoisonGasCloudEntity;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 的自定义实体注册入口。
 *
 * <h2>常见用途</h2>
 * <ul>
 *   <li>职业召唤物（例如追踪弹、幻影、无人机）</li>
 *   <li>播放 / 回放实体（参考 NoellesRoles 魔术师的播放皮套）</li>
 *   <li>自定义投掷物</li>
 * </ul>
 *
 * <h2>必须同步处理的事项</h2>
 * <ol>
 *   <li><b>实体类型注册</b>：{@code Registry.register(Registries.ENTITY_TYPE, ...)}</li>
 *   <li><b>属性注册</b>：如果需要血量 / 速度，用
 *       {@code FabricDefaultAttributeRegistry.register(...)}</li>
 *   <li><b>客户端渲染器</b>：在 {@code client/bootstrap/FangClientEntityRendererBootstrap}
 *       注册 {@code EntityRendererRegistry}，<b>必须</b>补，否则客户端进服会崩</li>
 *   <li><b>模型与贴图</b>：{@code assets/fangroles/textures/entity/...} + 模型类</li>
 *   <li><b>回放清理</b>：实体属于「非 CCA 状态」，回合结束必须显式清理，
 *       见 {@code bootstrap/FangRolesEventBootstrap} 的清理清单</li>
 *   <li><b>实体索引</b>：如果维护了「谁的实体」这类静态索引，
 *       断线、死亡、回合结束时都要清理，否则会内存泄漏</li>
 * </ol>
 */
public final class FangRolesEntities {
    private FangRolesEntities() {
    }

    /** 毒气弹投掷物；客户端用 FlyingItemEntityRenderer 显示物品贴图。 */
    public static final EntityType<PoisonGasBombEntity> POISON_GAS_BOMB = Registry.register(
            Registries.ENTITY_TYPE,
            FangRolesCore.id("poison_gas_bomb"),
            EntityType.Builder.create(PoisonGasBombEntity::new, SpawnGroup.MISC)
                    .dimensions(0.2F, 0.2F)
                    .build("poison_gas_bomb")
    );

    /** 毒气云本身不渲染模型，只负责服务端扩散与粒子；客户端用 EmptyEntityRenderer。 */
    public static final EntityType<PoisonGasCloudEntity> POISON_GAS_CLOUD = Registry.register(
            Registries.ENTITY_TYPE,
            FangRolesCore.id("poison_gas_cloud"),
            EntityType.Builder.create(PoisonGasCloudEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .build("poison_gas_cloud")
    );

    /**
     * 注册所有实体类型与属性。
     *
     * <p>由 {@code FangRolesBootstrap.init()} 调用。
     * 新增实体后记得同步补客户端渲染器注册。</p>
     */
    public static void init() {
        FangRolesCore.LOGGER.debug("[fangroles] 实体注册完成");
    }
}
