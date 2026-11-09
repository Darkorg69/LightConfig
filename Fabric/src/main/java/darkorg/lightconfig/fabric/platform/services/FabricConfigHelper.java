package darkorg.lightconfig.fabric.platform.services;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ConfigType;
import darkorg.lightconfig.common.api.ModConfig;
import darkorg.lightconfig.common.impl.AbstractModConfig;
import darkorg.lightconfig.common.platform.services.IConfigHelper;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricConfigHelper implements IConfigHelper {
    @Override
    public void registerClientConfig(ModConfig pSpec) {
        if (pSpec.getConfigType() != ConfigType.CLIENT) {
            throw new IllegalArgumentException("Expected CLIENT config type, got: " + pSpec.getConfigType());
        }
        if (pSpec instanceof AbstractModConfig baseSpec) {
            baseSpec.setConfigPath(FabricLoader.getInstance().getConfigDir().resolve(pSpec.getModId()).resolve(pSpec.getFileName() + ".toml"));
        }
        pSpec.load();
        LightConfig.registerSpec(pSpec);
        LightConfig.LOGGER.debug("Registered client config: {}", pSpec.getFileName());
    }

    @Override
    public void registerServerConfig(ModConfig pSpec) {
        if (pSpec.getConfigType() != ConfigType.SERVER) {
            throw new IllegalArgumentException("Expected SERVER config type, got: " + pSpec.getConfigType());
        }
        LightConfig.registerSpec(pSpec);
        LightConfig.LOGGER.debug("Registered server config: {}", pSpec.getFileName());
    }
}
