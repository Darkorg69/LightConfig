package darkorg.lightconfig.forge.network.packets.client;

import darkorg.lightconfig.common.network.packets.client.SyncDataS2CAbstractPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SyncDataS2CForgePacket extends SyncDataS2CAbstractPacket {
    public SyncDataS2CForgePacket(CompoundTag pData) {
        super(pData);
    }

    public SyncDataS2CForgePacket(FriendlyByteBuf pBuf) {
        super(pBuf);
    }

}
