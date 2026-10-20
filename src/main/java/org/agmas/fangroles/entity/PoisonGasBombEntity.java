package org.agmas.fangroles.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.agmas.fangroles.FangRolesEntities;
import org.agmas.fangroles.ModItems;
import org.agmas.noellesroles.api.timekeeper.TimekeeperSnapshotApi;

/**
 * 毒气弹投掷物：碰撞后生成毒气云并消失。
 *
 * <p>云的实际落点可能贴在半砖、楼梯侧面等不完整方块上，
 * 因此这里只负责把炸弹位置交给 PoisonGasCloudEntity，由云自己寻找安全种子。</p>
 */
public class PoisonGasBombEntity extends ThrownItemEntity {
    public PoisonGasBombEntity(EntityType<?> ignored, World world) {
        super(FangRolesEntities.POISON_GAS_BOMB, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.POISON_GAS_BOMB;
    }

    @Override
    public void tick() {
        /*
         * 时停回溯播放期间不推进飞行中的毒气弹，位置/速度/年龄由参与者快照逐帧恢复；
         * 否则回溯中途可能碰撞生成一个历史帧里不该存在的毒气云。
         */
        if (this.getWorld() instanceof ServerWorld serverWorld
                && TimekeeperSnapshotApi.isRewinding(serverWorld)) {
            return;
        }
        super.tick();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getWorld() instanceof ServerWorld world) {
            PoisonGasCloudEntity cloud = new PoisonGasCloudEntity(FangRolesEntities.POISON_GAS_CLOUD, world);
            cloud.setPosition(this.getX(), this.getY(), this.getZ());
            if (this.getOwner() instanceof ServerPlayerEntity owner) {
                cloud.setOwnerUuid(owner.getUuid());
            }
            world.spawnEntity(cloud);
            this.discard();
        }
    }
}
