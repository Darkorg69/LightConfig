package darkorg.lightconfig.fabric.event;

import darkorg.lightconfig.common.event.ModClientEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public abstract class FabricClientEvents {
    public static void init() {
        onDisconnect();
    }

    public static void onDisconnect() {
        ClientPlayConnectionEvents.DISCONNECT.register((pClientPacketListener, pMinecraft) -> ModClientEvents.onDisconnect());
    }
}
