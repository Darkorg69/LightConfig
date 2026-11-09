package darkorg.lightconfig.common.network;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ModConfig;
import darkorg.lightconfig.common.impl.ConfigSyncData;
import net.minecraft.nbt.CompoundTag;

public final class ClientBoundPacketHandler {
    public static void receiveSyncDataS2CPacket(CompoundTag pData, boolean pApply) {
        if (!pApply) {
            LightConfig.LOGGER.debug("Ignoring server config sync on the integrated server");
            return;
        }

        ConfigSyncData syncData = ConfigSyncData.fromNBT(pData);
        for (ModConfig config : LightConfig.getConfigs()) {
            if (config.getModId().equals(syncData.modId()) && config.getFileName().equals(syncData.fileName())) {
                config.setSyncedValues(syncData.values());
                LightConfig.LOGGER.debug("Received synced config for mod: {} ({})", syncData.modId(), syncData.fileName());
                return;
            }
        }
        LightConfig.LOGGER.warn("No config found for mod: {} ({})", syncData.modId(), syncData.fileName());
    }
}
