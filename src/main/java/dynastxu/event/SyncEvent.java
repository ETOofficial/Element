package dynastxu.event;

import dynastxu.api.ILivingEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class SyncEvent {
    /**
     * 同步玩家的元素相关数据
     * @param player 玩家实体
     */
    public static void syncPlayerData(LivingEntity player) {
        // 假设 ILivingEntityData 提供了 syncAttachedElements 方法用于同步
        ((ILivingEntityData) player).element$syncDatas();

        // 可选：通过网络发送同步包给客户端
        // Network.sendPlayerDataSynchronizationPacket(player);
    }

    public static void syncEntityData(ServerPlayNetworkHandler handler) {
        // 同步玩家附近的实体
        for (Entity entity : handler.player.getWorld().getOtherEntities(
                null,
                handler.player.getBoundingBox().expand(32),
                e -> e instanceof LivingEntity
        )) {
            ((ILivingEntityData) entity).element$syncDatas();
        }
    }

    public static void sync(ServerPlayNetworkHandler handler){
        syncPlayerData(handler.player);
        syncEntityData(handler);
    }
}
