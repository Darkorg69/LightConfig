package darkorg.lightconfig.fabric;

import darkorg.lightconfig.common.LightConfig;
import net.fabricmc.api.ModInitializer;

public final class LightConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LightConfig.init();
    }
}
