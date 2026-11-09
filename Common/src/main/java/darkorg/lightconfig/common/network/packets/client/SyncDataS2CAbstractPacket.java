package darkorg.lightconfig.common.network.packets.client;

import darkorg.lightconfig.common.network.packets.AbstractPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class SyncDataS2CAbstractPacket extends AbstractPacket {
    public SyncDataS2CAbstractPacket(CompoundTag pData) {
        super(pData);
    }

    public SyncDataS2CAbstractPacket(FriendlyByteBuf pBuf) {
        super(pBuf);
    }
}
