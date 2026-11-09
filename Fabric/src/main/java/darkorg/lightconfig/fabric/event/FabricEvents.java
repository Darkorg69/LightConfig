package darkorg.lightconfig.fabric.event;

import darkorg.lightconfig.common.event.ModEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public abstract class FabricEvents {
    public static void init() {
        onServerStarting();
        onServerStopping();
        onPlayReady();
    }

    private static void onServerStarting() {
        ServerLifecycleEvents.SERVER_STARTING.register(ModEvents::onServerStarting);
    }

    private static void onServerStopping() {
        ServerLifecycleEvents.SERVER_STOPPING.register(ModEvents::onServerStopping);
    }

    private static void onPlayReady() {
        ServerPlayConnectionEvents.JOIN.register((pServerGamePacketListener, pPacketSender, pMinecraftServer) -> ModEvents.onPlayReady(pServerGamePacketListener, pMinecraftServer));
    }
}
