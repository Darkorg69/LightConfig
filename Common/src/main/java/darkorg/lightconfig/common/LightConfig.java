package darkorg.lightconfig.common;

import darkorg.lightconfig.common.api.ModConfig;
import darkorg.lightconfig.common.event.ModEvents;
import darkorg.lightconfig.common.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LightConfig {
    public static final String MOD_ID = "lightconfig";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final List<ModConfig> CONFIGS = new ArrayList<>();

    public static void register(ModConfig pSpec) {
        switch (pSpec.getConfigType()) {
            case CLIENT -> Services.CONFIG_HELPER.registerClientConfig(pSpec);
            case SERVER -> Services.CONFIG_HELPER.registerServerConfig(pSpec);
        }
        ModEvents.onConfigRegistered(pSpec);
    }

    public static void registerSpec(ModConfig pSpec) {
        for (ModConfig existingSpec : CONFIGS) {
            if (existingSpec.getConfigType() == pSpec.getConfigType() && existingSpec.getModId().equals(pSpec.getModId()) && existingSpec.getFileName().equals(pSpec.getFileName())) {
                throw new IllegalArgumentException("Duplicate config registration: mod '" + pSpec.getModId() + "' already has a " + pSpec.getConfigType() + " config named '" + pSpec.getFileName() + "'");
            }
        }
        CONFIGS.add(pSpec);
    }

    public static List<ModConfig> getConfigs() {
        return Collections.unmodifiableList(CONFIGS);
    }

    public static void init() {
        LOGGER.debug("COMMON initialization started on {}! We are currently in a {} environment!", Services.PLATFORM_HELPER.getPlatformName(), Services.PLATFORM_HELPER.getEnvironmentName());
    }

    public static void initClient() {
        LOGGER.debug("CLIENT initialization started on {}! We are currently in a {} environment!", Services.PLATFORM_HELPER.getPlatformName(), Services.PLATFORM_HELPER.getEnvironmentName());
        Services.EVENT_HELPER.initClientListeners();
        Services.EVENT_HELPER.initListeners();
        Services.PACKET_HELPER.registerClientBoundPackets();
    }

    public static void initServer() {
        LOGGER.debug("SERVER initialization started on {}! We are currently in a {} environment!", Services.PLATFORM_HELPER.getPlatformName(), Services.PLATFORM_HELPER.getEnvironmentName());
        Services.EVENT_HELPER.initListeners();
    }
}
