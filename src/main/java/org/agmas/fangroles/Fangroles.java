package org.agmas.fangroles;

import net.fabricmc.api.ModInitializer;
import org.agmas.fangroles.bootstrap.FangRolesBootstrap;

/**
 * fangroles 的 Fabric 服务端 / 公共主入口。
 *
 * <p>这个类<b>只做一件事</b>：调用总启动编排器 {@link FangRolesBootstrap#init()}。</p>
 *
 * <p>后续新增职业、事件、网络包、经济、回放、商店等注册时，
 * <b>不要</b>把注册逻辑写回这里。请按职责放进 {@code bootstrap/}、{@code registry/}、
 * {@code roles/<role_id>/} 等专用类，再在 {@code FangRolesBootstrap} 里按顺序调用。</p>
 *
 * <p>这样做的原因：NoellesRoles 早期把所有注册堆在主入口，导致初始化顺序无法维护、
 * 也容易出现循环依赖。本模板从一开始就采用同款「入口只编排」结构。</p>
 */
public class Fangroles implements ModInitializer {
    @Override
    public void onInitialize() {
        FangRolesBootstrap.init();
    }
}
