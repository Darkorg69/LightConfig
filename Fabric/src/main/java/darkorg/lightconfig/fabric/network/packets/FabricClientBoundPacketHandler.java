package darkorg.lightconfig.fabric.network.packets;

import darkorg.lightconfig.common.network.ClientBoundPacketHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

@Environment(EnvType.CLIENT)
public final class FabricClientBoundPacketHandler {
    public static void receiveSyncData(Minecraft pMinecraft, ClientPacketListener pClientPacketListener, FriendlyByteBuf pBuf, PacketSender pPacketSender) {

        CompoundTag data = pBuf.readNbt();
        if (data != null) {
            boolean apply = pMinecraft.getSingleplayerServer() == null;
            pMinecraft.execute(() -> {

                ClientBoundPacketHandler.receiveSyncDataS2CPacket(data, apply);
            });
        }
    }
}
