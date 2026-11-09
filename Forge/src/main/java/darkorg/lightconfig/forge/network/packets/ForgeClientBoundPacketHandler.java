package darkorg.lightconfig.forge.network.packets;

import darkorg.lightconfig.common.network.ClientBoundPacketHandler;
import darkorg.lightconfig.forge.network.packets.client.SyncDataS2CForgePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ForgeClientBoundPacketHandler {
    public static void receiveSyncData(SyncDataS2CForgePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        NetworkEvent.Context context = pContext.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {

                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
                    boolean apply = Minecraft.getInstance().getSingleplayerServer() == null;
                    ClientBoundPacketHandler.receiveSyncDataS2CPacket(pPacket.data, apply);
                });
            });
            context.setPacketHandled(true);
        }
    }
}
