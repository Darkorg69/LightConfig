package darkorg.lightconfig.forge.network;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.forge.network.packets.ForgeClientBoundPacketHandler;
import darkorg.lightconfig.forge.network.packets.client.SyncDataS2CForgePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public abstract class ForgePackets {
    private static final SimpleChannel INSTANCE = ChannelBuilder
            .named(new ResourceLocation(LightConfig.MOD_ID, "messages"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions((pStatus) -> true)
            .serverAcceptedVersions((pStatus) -> true)
            .simpleChannel();

    private static int id = 0;

    private static int id() {
        return id++;
    }

    public static void initServerToClientPackets() {
        INSTANCE.messageBuilder(SyncDataS2CForgePacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncDataS2CForgePacket::new)
                .encoder(SyncDataS2CForgePacket::encode)
                .consumerMainThread(ForgeClientBoundPacketHandler::receiveSyncData)
                .add();
    }

    public static <Packet> void sendToPlayer(Packet pPacket, ServerPlayer pServerPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> pServerPlayer), pPacket);
    }

    public static boolean canSend(ServerPlayer pServerPlayer) {
        return INSTANCE.isRemotePresent(pServerPlayer.connection.connection);
    }
}
