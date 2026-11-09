package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.api.ConfigType;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class ClientConfig extends AbstractModConfig {
    public ClientConfig(String pModId, String pFileName, Map<String, ConfigValue<?>> pValues) {
        super(pModId, pFileName, pValues);
    }

    @Override
    public ConfigType getConfigType() {
        return ConfigType.CLIENT;
    }

    @Override
    public <T> T get(String pKey) {
        Optional<ConfigValue<?>> value = getValue(pKey);
        if (value.isPresent()) {
            return (T) value.get().get();
        }
        return null;
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
        return false;
    }

    @Override
    public void clearSyncedValues() {

    }

    @Override
    public Map<String, Object> getSyncedValues() {
        return Collections.emptyMap();
    }

    @Override
    public void setSyncedValues(Map<String, Object> pValues) {

    }
}
