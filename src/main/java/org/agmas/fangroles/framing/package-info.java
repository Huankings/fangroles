/**
 * fangroles 的伪装（Framing）实现区。
 *
 * <p>「伪装」指让玩家或尸体在别人眼里呈现成另一个身份 / 外观，
 * 或者与 Wathe 的 framing（派系伪装、商店伪装条目）相关的共享内容。</p>
 *
 * <h2>外观伪装优先走 API</h2>
 * <ul>
 *   <li>玩家皮肤：{@code PlayerAppearanceApi.registerPlayerSkin(...)}</li>
 *   <li>客户端尸体皮肤：{@code PlayerAppearanceApi.registerBodySkin(...)}</li>
 *   <li>服务端生成尸体外观 UUID：{@code BodyAppearanceApi.register(...)}</li>
 *   <li>准心名字：{@code RoleNameHudApi.registerName(...)}</li>
 *   <li>同伙提示：{@code RoleNameHudApi.registerCohortState(...)} /
 *       {@code registerCohortTargetState(...)}</li>
 *   <li>低心情幻觉手持物：{@code PsychosisItemApi}（纯客户端视觉）</li>
 * </ul>
 *
 * <h2>三条必须守住的边界</h2>
 * <ol>
 *   <li><b>伪装是视觉，不是身份。</b>服务端真实职业 / 尸体 owner 不能因为伪装而改变，
 *       否则胜利判定、死亡奖励和回放都会错乱。</li>
 *   <li><b>防套娃。</b>要拿目标原本皮肤时用
 *       {@code PlayerAppearanceApi.resolveOriginalSkinTextures(...)}，
 *       不要读当前已被覆盖过的皮肤。</li>
 *   <li><b>可见性不等于权限。</b>隐藏了准心目标不代表不能攻击，
 *       服务端仍要重新校验（详见 {@code visibility/FangRolesTargetVisibilityHandlers}）。</li>
 * </ol>
 *
 * <h2>共享商店伪装条目</h2>
 * <p>如果需要多个职业共用同一批伪装商品，把条目定义收敛到本包的一个共享类里
 * （参考 NoellesRoles 的 {@code NoellesFramingShopEntries}），
 * 不要让每个职业各写一份，否则改价格要改很多处。</p>
 */
package org.agmas.fangroles.framing;
