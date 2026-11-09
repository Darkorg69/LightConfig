package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ConfigType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ServerConfig extends AbstractModConfig {
    private final Map<String, Object> syncedValues = new LinkedHashMap<>();
    private boolean connected = false;

    public ServerConfig(String pModId, String pFileName, Map<String, ConfigValue<?>> pValues) {
        super(pModId, pFileName, pValues);
        bindSyncedValues();
    }

    private void bindSyncedValues() {
        for (ConfigValue value : getValues().values()) {
            value.bindSyncedValue(() -> Optional.ofNullable(this.syncedValues.get(value.getKey())));
        }
    }

    @Override
    public ConfigType getConfigType() {
        return ConfigType.SERVER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String pKey) {
        return (T) getValue(pKey).map(ConfigValue::get).orElse(null);
    }

    @Override
    public void load() {
        if (getConfigPath() != null) {
            ConfigLoader.loadStatic(this, getConfigPath());
        }
    }

    @Override
    public void save() {
        if (getConfigPath() != null) {
            ConfigLoader.saveStatic(this, getConfigPath());
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public void clearSyncedValues() {
        this.syncedValues.clear();
        this.connected = false;
    }

    @Override
    public Map<String, Object> getSyncedValues() {
        return Collections.unmodifiableMap(this.syncedValues);
    }

    @Override
    public void setSyncedValues(Map<String, Object> pValues) {
        Map<String, Object> parsedValues = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : pValues.entrySet()) {
            ConfigValue<?> value = getValues().get(entry.getKey());
            if (value == null) {
                LightConfig.LOGGER.warn("Ignoring unknown synced key '{}' for config '{}'", entry.getKey(), getFileName());
                continue;
            }
            try {
                parsedValues.put(entry.getKey(), value.parse(entry.getValue()));
            } catch (RuntimeException pException) {
                LightConfig.LOGGER.warn("Ignoring invalid synced value '{}' for '{}' in config '{}': {}",
                        entry.getValue(), entry.getKey(), getFileName(), pException.getMessage());
            }
        }

        this.syncedValues.clear();
        this.syncedValues.putAll(parsedValues);
        this.connected = true;
        LightConfig.LOGGER.info("Applied {} synced value(s) to config '{}': {}",
                parsedValues.size(), getFileName(), parsedValues);
    }
}
