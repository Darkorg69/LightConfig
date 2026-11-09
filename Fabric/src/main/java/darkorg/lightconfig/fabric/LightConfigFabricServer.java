package darkorg.lightconfig.fabric;

import darkorg.lightconfig.common.LightConfig;
import net.fabricmc.api.DedicatedServerModInitializer;

public final class LightConfigFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LightConfig.initServer();
    }
}
