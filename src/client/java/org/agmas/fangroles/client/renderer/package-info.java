/**
 * fangroles 的客户端渲染实现区。
 *
 * <p>注册编排在 {@code client/bootstrap/FangClientEntityRendererBootstrap}；本包放渲染类与模型类。</p>
 *
 * <h2>典型内容</h2>
 * <ul>
 *   <li>实体渲染器（继承 {@code EntityRenderer}）</li>
 *   <li>实体模型（继承 {@code EntityModel}）</li>
 *   <li>方块实体渲染器</li>
 *   <li>自定义渲染层 / 特征层（FeatureRenderer）</li>
 * </ul>
 *
 * <h2>贴图资源</h2>
 * <pre>{@code
 * src/main/resources/assets/fangroles/textures/entity/<name>.png
 * src/main/resources/assets/fangroles/textures/block/<name>.png
 * }</pre>
 *
 * <h2>写渲染前的检查清单</h2>
 * <ol>
 *   <li><b>Wathe 是否已经提供 API？</b>
 *       外观、尸体皮肤、准心、HUD、名字、雾效都有公开 API，
 *       见 {@code AGENTS.md} 的「公开 API 优先表」。</li>
 *   <li><b>是否会与旁观 / 创造模式冲突？</b>
 *       渲染类应尊重 {@code GameFunctions.isPlayerSpectatingOrCreative(...)} 等判断。</li>
 *   <li><b>是否会破坏玩家设置？</b>
 *       改 FOV、相机、雾距时必须可恢复、可叠加，
 *       不要直接覆盖玩家自己的设置。</li>
 *   <li><b>是否需要考虑 Iris / Sodium？</b>
 *       雾效走 {@code FogOverrideApi}（已兼容 Iris 标准 fog uniform），
 *       不要直接依赖 Iris 私有类。</li>
 * </ol>
 */
package org.agmas.fangroles.client.renderer;
