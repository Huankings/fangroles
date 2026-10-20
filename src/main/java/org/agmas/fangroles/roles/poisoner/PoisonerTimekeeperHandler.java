package org.agmas.fangroles.roles.poisoner;

import org.agmas.noellesroles.api.timekeeper.TimekeeperSnapshotApi;

/**
 * 毒师时停回溯接入入口。
 *
 * <p>必须在首张历史快照生成前把参与者注册进 NoellesRoles 的时停 API；
 * 本方法由 FangRolesBootstrap 在模组初始化阶段调用，早于任何开局采样。</p>
 */
public final class PoisonerTimekeeperHandler {
    private static boolean initialized;

    private PoisonerTimekeeperHandler() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        TimekeeperSnapshotApi.registerParticipant(new PoisonerTimekeeperSnapshotParticipant());
    }
}
