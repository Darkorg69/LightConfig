package darkorg.lightconfig.common.platform.services;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface IPacketHelper {
    <T> void sendToPlayer(ServerPlayer pServerPlayer, T pPacket);

    void registerClientBoundPackets();

    boolean canSend(ServerPlayer pServerPlayer);

    void sendSyncDataS2C(ServerPlayer pServerPlayer, CompoundTag pData);
}
