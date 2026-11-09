package darkorg.lightconfig.fabric;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.event.ModEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public final class LightConfigFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LightConfig.initClient();
        ModEvents.startClientWatchers(pRunnable -> Minecraft.getInstance().execute(pRunnable));
    }
}
