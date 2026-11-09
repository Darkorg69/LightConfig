package darkorg.lightconfig.fabric.network;

import darkorg.lightconfig.common.network.ModPackets;
import darkorg.lightconfig.fabric.network.packets.FabricClientBoundPacketHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FabricPackets {
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SYNC_DATA_S2C, FabricClientBoundPacketHandler::receiveSyncData);
    }
}
