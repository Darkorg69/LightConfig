package darkorg.lightconfig.common.event;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ModConfig;

public abstract class ModClientEvents {
    public static void onDisconnect() {
        LightConfig.LOGGER.debug("Clearing synced config values");
        for (ModConfig spec : LightConfig.getConfigs()) {
            spec.clearSyncedValues();
        }
        LightConfig.LOGGER.debug("Disconnected, cleared synced config values");
    }
}
