package org.agmas.fangroles.client.bootstrap;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 客户端实体渲染器注册入口。
 *
 * <h2>硬性要求</h2>
 * <p>只要 {@code FangRolesEntities} 里新增了自定义实体类型，
 * <b>必须</b>在本类注册对应渲染器，否则客户端加入服务器时会崩
 * （报错通常是「no renderer registered for entity type」）。</p>
 *
 * <h2>写法</h2>
 * <pre>{@code
 * EntityRendererRegistry.register(FangRolesEntities.SOME_ENTITY, SomeEntityRenderer::new);
 * }</pre>
 *
 * <h2>相关的客户端资源</h2>
 * <ul>
 *   <li>模型类放 {@code client/renderer/}（继承 {@code EntityModel} /
 *       {@code EntityRenderer}）。</li>
 *   <li>贴图放 {@code assets/fangroles/textures/entity/<name>.png}。</li>
 *   <li>如果需要动画（例如播放型实体的动作重演），
 *       动画数据来源要明确来自服务端同步，不要客户端自己编。</li>
 * </ul>
 *
 * <h2>注意</h2>
 * <p>本类属于客户端源码集（{@code src/client/java}），
 * 不要把这些 import 写进 {@code src/main} 的类里，否则服务端会因为找不到
 * 客户端类而启动失败。</p>
 */
public final class FangClientEntityRendererBootstrap {
    private FangClientEntityRendererBootstrap() {
    }

    /**
     * 注册所有实体渲染器。
     *
     * <p>目前没有自定义实体，空实现。
     * 新增实体后必须回到本类补注册。</p>
     */
    public static void register() {
        // 毒气弹用原版飞行物品渲染器显示物品贴图；毒气云本身无模型，只用服务端 dust 粒子表现。
        EntityRendererRegistry.register(FangRolesEntities.POISON_GAS_BOMB, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(FangRolesEntities.POISON_GAS_CLOUD, EmptyEntityRenderer::new);
        FangRolesCore.LOGGER.debug("[fangroles] 客户端实体渲染器注册完成");
    }
}
