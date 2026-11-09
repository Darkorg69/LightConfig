package darkorg.lightconfig.common.network.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class AbstractPacket {
    public CompoundTag data;

    public AbstractPacket() {
        this(new CompoundTag());
    }

    public AbstractPacket(CompoundTag pData) {
        this.data = pData;
    }

    public AbstractPacket(FriendlyByteBuf pBuf) {
        this(pBuf.readNbt());
    }

    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeNbt(this.data);
    }
}
