package darkorg.lightconfig.common.api;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public interface ModConfig {
    String getModId();

    String getFileName();

    Path getConfigPath();

    ConfigType getConfigType();

    Map<String, ConfigValue<?>> getValues();

    default Optional<ConfigValue<?>> getValue(String pKey) {
        return Optional.ofNullable(getValues().get(pKey));
    }

    <T> T get(String pKey);

    void load();

    void reload();

    void save();

    boolean isConnected();

    void clearSyncedValues();

    Map<String, Object> getSyncedValues();

    void setSyncedValues(Map<String, Object> pValues);

    Map<String, Object> collectAllValues();
}
