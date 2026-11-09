package darkorg.lightconfig.common.platform.services;

import darkorg.lightconfig.common.api.ModConfig;

public interface IConfigHelper {
    void registerClientConfig(ModConfig pSpec);

    void registerServerConfig(ModConfig pSpec);
}
