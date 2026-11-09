package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.api.ModConfig;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractModConfig implements ModConfig {
    private final String modId;
    private final String fileName;
    private final Map<String, ConfigValue<?>> values;
    private Path configPath;

    protected AbstractModConfig(String pModId, String pFileName, Map<String, ConfigValue<?>> pValues) {
        this.modId = pModId;
        this.fileName = pFileName;
        this.values = new LinkedHashMap<>(pValues);
    }

    @Override
    public String getModId() {
        return this.modId;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public Map<String, ConfigValue<?>> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public Path getConfigPath() {
        return this.configPath;
    }

    public void setConfigPath(Path pConfigPath) {
        this.configPath = pConfigPath;
    }

    @Override
    public void reload() {
        if (this.configPath != null) {
            ConfigLoader.reloadStatic(this, this.configPath);
        }
    }

    @Override
    public Map<String, Object> collectAllValues() {
        Map<String, Object> collectedValues = new LinkedHashMap<>();
        for (Map.Entry<String, ConfigValue<?>> entry : this.values.entrySet()) {
            collectedValues.put(entry.getKey(), entry.getValue().get());
        }
        return collectedValues;
    }
}
