package darkorg.lightconfig.forge.platform.services;

import darkorg.lightconfig.common.platform.services.IPacketHelper;
import darkorg.lightconfig.forge.network.ForgePackets;
import darkorg.lightconfig.forge.network.packets.client.SyncDataS2CForgePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class ForgePacketHelper implements IPacketHelper {
    @Override
    public <T> void sendToPlayer(ServerPlayer pServerPlayer, T pPacket) {
        ForgePackets.sendToPlayer(pPacket, pServerPlayer);
    }

    @Override
    public void registerClientBoundPackets() {
        ForgePackets.initServerToClientPackets();
    }

    @Override
    public boolean canSend(ServerPlayer pServerPlayer) {
        return ForgePackets.canSend(pServerPlayer);
    }

    @Override
    public void sendSyncDataS2C(ServerPlayer pServerPlayer, CompoundTag pData) {
        sendToPlayer(pServerPlayer, new SyncDataS2CForgePacket(pData));
    }
}
