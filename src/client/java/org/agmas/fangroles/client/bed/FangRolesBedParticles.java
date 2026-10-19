package org.agmas.fangroles.client.bed;

import dev.doctor4t.wathe.api.bed.BedParticleRegistry;
import org.agmas.fangroles.registry.FangRolesCore;

/**
 * fangroles 床粒子（Bed particle）注册聚合入口。
 *
 * <p>与 NoellesRoles 的 {@code NoellesRolesBedParticles} 结构对齐：
 * 本类<b>只</b>负责初始化顺序与幂等性，具体每个职业的床粒子 provider
 * 放在 {@code client/roles/<role_id>/<RoleName>BedParticleHandler}，
 * 由本类逐个调用它们的 {@code init()}。</p>
 *
 * <h2>为什么床粒子要和床效果分开</h2>
 * <p>Wathe 的床系统是「服务端裁定 + 客户端表现」两层结构：</p>
 * <ul>
 *   <li><b>服务端</b>：{@link dev.doctor4t.wathe.api.bed.BedEffectRegistry}
 *       负责「识别塞床物品 → 把效果与归属者同步到床头方块实体 → 睡觉后统一结算
 *       → 给回放保留 {@code bed_effect} 字段」。
 *       接入点见 {@code bed/FangRolesBedEffects}。</li>
 *   <li><b>客户端</b>：{@link BedParticleRegistry} 只负责「这张床上该显示什么粒子」。
 *       接入点就是本类。</li>
 * </ul>
 * <p>把两者拆开的原因是：粒子是纯视觉，不影响床效果的真实触发、所有权、消耗和回放。
 * 所以扩展<b>不要</b>再注入 {@code TrimmedBedBlockEntity.clientTick}
 * 来给自己加粒子 —— Wathe 已经把这部分公开成 provider 注册表。</p>
 *
 * <h2>provider 的执行语义（写 handler 前必须理解）</h2>
 * <ol>
 *   <li>provider 按 <b>priority 从高到低</b>执行。</li>
 *   <li>返回 <b>{@code true}</b> 表示「本次已经处理」，Wathe 会<b>停止</b>继续询问更低优先级 provider。</li>
 *   <li>返回 <b>{@code false}</b> 表示「这张床不是我的效果」，Wathe 会继续往下问。</li>
 *   <li>Wathe 原生蝎子床粒子注册在 <b>{@code Integer.MIN_VALUE}</b>（最低优先级），
 *       所以扩展可以直接覆盖或补充它的表现。</li>
 * </ol>
 * <p><b>最容易写错的地方</b>：如果 provider 判断出「这是我的效果，但当前观察者无权看到」，
 * 应该返回 {@code true}（已识别、故意不生成粒子），而<b>不是</b>返回 {@code false}。
 * 返回 {@code false} 会让链路继续走到更低优先级的 provider，
 * 可能出现「没权限却仍然显示了别人的床粒子」的信息泄露。</p>
 *
 * <h2>id 命名与查找方式</h2>
 * <p>{@code registerProvider(id, ...)} 的 {@code id} 是字符串，用于替换同 id 的旧 provider。
 * 建议直接用床效果的 {@code Identifier} 字符串，并登记在
 * {@code registry/FangRolesEventIds}，这样注册处与判断处引用同一个常量，
 * 不会因为手写字符串不一致而出现「注册了但没生效」。</p>
 * <pre>{@code
 * // registry/FangRolesEventIds.java
 * public static final Identifier SOME_BED_EFFECT_ID = FangRolesCore.id("some_bed_effect");
 *
 * // client/roles/<role_id>/<RoleName>BedParticleHandler.java
 * BedParticleRegistry.registerProvider(
 *         FangRolesEventIds.SOME_BED_EFFECT_ID.toString(),   // 与判断处共用同一常量
 *         100,                                               // priority 越大越先执行
 *         context -> {
 *             // getBedEffect() 返回的是 BedEffectRegistry 写入的 Identifier 字符串，可能为 null
 *             if (!FangRolesEventIds.SOME_BED_EFFECT_ID.toString().equals(context.bed().getBedEffect())) {
 *                 return false;    // 不是我的效果，交给更低优先级
 *             }
 *             // ... 权限判断：无权限时 return true（已识别但故意不画）
 *             // ... 稀疏刷新：context.world().getRandom().nextBetween(0, 20) < 17 之类
 *             context.world().addParticle(粒子, x, y, z, vx, vy, vz);
 *             return true;         // 已处理，停止链路
 *         });
 * }</pre>
 *
 * <h2>性能约束</h2>
 * <ul>
 *   <li>provider 由床方块实体的客户端 tick <b>每 tick</b> 调用，
 *       所以内部判断要尽量便宜（先比字符串，再判权限，最后才考虑生成粒子）。</li>
 *   <li>必须做<b>稀疏刷新</b>，不要每 tick 都生成粒子。
 *       Wathe 原生蝎子床与 NoellesRoles 炸弹客床都使用
 *       {@code nextBetween(0, 20) < 17}（约 15% 概率生成），可直接沿用。</li>
 *   <li>不要在这里做网络请求或读远端数据。</li>
 * </ul>
 *
 * <h2>客户端专属：不要把本类引入 src/main</h2>
 * <p>{@link BedParticleRegistry} 与
 * {@link dev.doctor4t.wathe.api.bed.BedParticleContext} 引用了
 * {@code MinecraftClient} / {@code ClientWorld} / {@code ClientPlayerEntity}
 * 等<b>客户端专属类</b>（位于 Wathe 的 {@code src/main/java} 但只在客户端使用）。
 * 因此本类以及各职业的 {@code *BedParticleHandler} 必须放在
 * {@code src/client/java}，<b>绝对不要</b>被 {@code src/main} 里的类引用，
 * 否则服务端会因为找不到客户端类而启动失败。</p>
 *
 * <h2>调用时机</h2>
 * <p>由 {@code client/bootstrap/FangClientParticleBootstrap#register()} 调用，
 * 和粒子工厂、托盘粒子放在一起，保证「客户端粒子相关注册」集中在一处。</p>
 */
public final class FangRolesBedParticles {

    /** 幂等标记：Wathe 的注册表虽然允许同 id 替换，但重复初始化会做无谓的排序与重建。 */
    private static boolean initialized;

    private FangRolesBedParticles() {
    }

    /**
     * 注册所有职业的床粒子 provider。
     *
     * <p>新增一个需要床粒子的职业时，在 {@code client/roles/<role_id>/} 下建
     * {@code <RoleName>BedParticleHandler.init()}，并在这里补一行调用，
     * 同时写中文注释说明该床效果的视觉语义（黑烟 / 毒雾 / 火花等）
     * 以及为什么选这个 priority。</p>
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        // ---- 各职业床粒子 provider 注册区（当前为空）----
        //
        // 新增示例（取消注释前请先建好对应职业的 handler 与 EventIds 常量）：
        // SomeRoleBedParticleHandler.init();

        FangRolesCore.LOGGER.debug("[fangroles] 床粒子 provider 注册完成（当前为空，Wathe 默认蝎子床粒子仍由本体最低优先级 provider 负责）");
    }
}
