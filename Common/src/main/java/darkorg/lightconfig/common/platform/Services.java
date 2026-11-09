package darkorg.lightconfig.common.platform;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.platform.services.IConfigHelper;
import darkorg.lightconfig.common.platform.services.IEventHelper;
import darkorg.lightconfig.common.platform.services.IPacketHelper;
import darkorg.lightconfig.common.platform.services.IPlatformHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class Services {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightConfig.MOD_ID);

    public static final IConfigHelper CONFIG_HELPER = load(IConfigHelper.class);
    public static final IEventHelper EVENT_HELPER = load(IEventHelper.class);
    public static final IPlatformHelper PLATFORM_HELPER = load(IPlatformHelper.class);
    public static final IPacketHelper PACKET_HELPER = load(IPacketHelper.class);

    private Services() {
    }

    private static <T> T load(Class<T> pService) {
        T loadedService = ServiceLoader.load(pService).findFirst().orElseThrow(
                () -> new NullPointerException("Failed to load service for " + pService.getName()));

        LOGGER.debug("Loaded {} for service {}", loadedService, pService);
        return loadedService;
    }
}
