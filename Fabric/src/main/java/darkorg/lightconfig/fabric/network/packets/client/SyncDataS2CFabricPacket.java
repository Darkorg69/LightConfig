package darkorg.lightconfig.fabric.network.packets.client;

import darkorg.lightconfig.common.network.ModPackets;
import darkorg.lightconfig.common.network.packets.client.SyncDataS2CAbstractPacket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SyncDataS2CFabricPacket extends SyncDataS2CAbstractPacket implements FabricPacket {
    private static final PacketType<SyncDataS2CFabricPacket> TYPE = PacketType.create(ModPackets.SYNC_DATA_S2C, SyncDataS2CFabricPacket::new);

    public SyncDataS2CFabricPacket(CompoundTag pData) {
        super(pData);
    }

    public SyncDataS2CFabricPacket(FriendlyByteBuf pBuf) {
        super(pBuf);
    }

    @Override
    public void write(FriendlyByteBuf pBuf) {
        pBuf.writeNbt(this.data);
    }

    @Override
    public PacketType<SyncDataS2CFabricPacket> getType() {
        return TYPE;
    }
}
