package darkorg.lightconfig.forge.platform.services;

import darkorg.lightconfig.common.platform.services.IPlatformHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public final class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public Path getDefaultConfigDirectory() {
        return FMLPaths.GAMEDIR.get().resolve("defaultconfigs");
    }
}
