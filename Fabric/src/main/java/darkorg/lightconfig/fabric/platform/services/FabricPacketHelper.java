package darkorg.lightconfig.fabric.platform.services;

import darkorg.lightconfig.common.network.ModPackets;
import darkorg.lightconfig.common.platform.services.IPacketHelper;
import darkorg.lightconfig.fabric.network.FabricPackets;
import darkorg.lightconfig.fabric.network.packets.client.SyncDataS2CFabricPacket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class FabricPacketHelper implements IPacketHelper {
    @Override
    public <T> void sendToPlayer(ServerPlayer pServerPlayer, T pPacket) {
        ServerPlayNetworking.send(pServerPlayer, (FabricPacket) pPacket);
    }

    @Override
    public void registerClientBoundPackets() {
        FabricPackets.registerS2CPackets();
    }

    @Override
    public boolean canSend(ServerPlayer pServerPlayer) {
        return ServerPlayNetworking.canSend(pServerPlayer, ModPackets.SYNC_DATA_S2C);
    }

    @Override
    public void sendSyncDataS2C(ServerPlayer pServerPlayer, CompoundTag pData) {
        sendToPlayer(pServerPlayer, new SyncDataS2CFabricPacket(pData));
    }
}
