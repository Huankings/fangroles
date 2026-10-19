/**
 * fangroles 的自定义实体实现区。
 *
 * <p>注册编排在 {@code FangRolesEntities}，本包放实体类本身。</p>
 *
 * <h2>三种常见实体类型与注意事项</h2>
 * <ol>
 *   <li><b>召唤物 / 追踪物</b>：需要属性注册
 *       （{@code FabricDefaultAttributeRegistry}）、客户端渲染器，
 *       以及「谁召唤的」索引清理。</li>
 *   <li><b>播放 / 回放实体</b>（参考 NoellesRoles 魔术师皮套）：
 *       通常只用于视觉重演，不参与真实伤害判定。
 *       必须明确「渲染用的动作数据来自哪里」以及回合结束时如何回收。</li>
 *   <li><b>投射物</b>：命中判定优先复用原版投射物 + Wathe 的
 *       {@code GunShotApi}，不要自己维护一套伤害路径。</li>
 * </ol>
 *
 * <h2>强制清理清单（实体属于「非 CCA 状态」）</h2>
 * <p>只要引入实体，就必须在 {@code bootstrap/FangRolesEventBootstrap} 里补：
 * <ul>
 *   <li>回合结束时移除所有本模组实体。</li>
 *   <li>玩家断线 / 死亡时清掉指向该玩家的实体索引。</li>
 *   <li>如果维护了静态 {@code Map<UUID, Entity>}，同样要清理，否则内存泄漏。</li>
 * </ul>
 * </p>
 *
 * <h2>客户端必须同步注册渲染器</h2>
 * <p>新增实体类型后，必须在
 * {@code client/bootstrap/FangClientEntityRendererBootstrap} 注册
 * {@code EntityRendererRegistry}，否则客户端加入服务器时会因为缺少渲染器而崩溃。</p>
 */
package org.agmas.fangroles.entities;
