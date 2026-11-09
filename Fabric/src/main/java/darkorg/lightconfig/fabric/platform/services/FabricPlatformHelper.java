package darkorg.lightconfig.fabric.platform.services;

import darkorg.lightconfig.common.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getDefaultConfigDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("defaultconfigs");
    }
}
