package darkorg.lightconfig.common.impl;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlWriter;
import darkorg.lightconfig.common.LightConfig;
import darkorg.lightconfig.common.api.ModConfig;
import darkorg.lightconfig.common.impl.value.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigLoader {
    public static void saveStatic(ModConfig pSpec, Path pPath) {
        try {
            Files.createDirectories(pPath.getParent());

            CommentedConfig config = CommentedConfig.of(LinkedHashMap::new, InMemoryCommentedFormat.defaultInstance());
            writeValues(config, pSpec);
            StringWriter stringWriter = new StringWriter();
            new TomlWriter().write(config, stringWriter);
            Files.writeString(pPath, addBlankLinesBetweenEntries(stringWriter.toString()), StandardCharsets.UTF_8);
        } catch (IOException pException) {
            throw new RuntimeException("Failed to save config: " + pPath, pException);
        }
    }

    private static String addBlankLinesBetweenEntries(String pToml) {
        String[] lines = pToml.split("\\R", -1);
        int lastLine = lines.length - 1;
        while (lastLine >= 0 && lines[lastLine].isEmpty()) {
            lastLine--;
        }

        StringBuilder formatted = new StringBuilder(pToml.length() + lines.length);
        for (int i = 0; i <= lastLine; i++) {
            formatted.append(lines[i]).append('\n');
            boolean isValueLine = !lines[i].isEmpty() && !lines[i].startsWith("#");
            if (isValueLine && i < lastLine) {
                formatted.append('\n');
            }
        }
        return formatted.toString();
    }

    public static void loadStatic(ModConfig pSpec, Path pPath) {
        loadStatic(pSpec, pPath, false);
    }

    public static void reloadStatic(ModConfig pSpec, Path pPath) {
        loadStatic(pSpec, pPath, true);
    }

    private static void loadStatic(ModConfig pSpec, Path pPath, boolean pLogChanges) {
        Map<String, Object> previousValues = pLogChanges ? collectStoredValues(pSpec) : Map.of();

        if (!Files.exists(pPath)) {
            LightConfig.LOGGER.info("Config file '{}' not found, creating it from built-in defaults", pPath);
            restoreDefaults(pSpec);
            try {
                saveStatic(pSpec, pPath);
            } catch (RuntimeException pException) {
                LightConfig.LOGGER.error("Failed to create config file '{}'; using defaults in memory", pPath, pException);
            }
            return;
        }

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(pPath).autosave().build()) {
            fileConfig.load();
            readValues(fileConfig, pSpec, pPath);
        } catch (Exception pException) {
            LightConfig.LOGGER.error("Failed to load config from '{}', restoring defaults", pPath, pException);
            restoreDefaults(pSpec);
        }

        if (pLogChanges) {
            logChanges(pSpec, previousValues);
        }
    }

    private static void writeValues(CommentedConfig pConfig, ModConfig pSpec) {
        for (Map.Entry<String, ConfigValue<?>> entry : pSpec.getValues().entrySet()) {
            String key = entry.getKey();
            ConfigValue<?> value = entry.getValue();
            pConfig.set(key, toWritableValue(value.getStoredValue()));
            String comment = buildComment(value);
            if (!comment.isEmpty()) {
                pConfig.setComment(key, comment);
            }
        }
    }

    private static String buildComment(ConfigValue<?> pValue) {
        StringBuilder comment = new StringBuilder();
        String description = pValue.getComment();
        if (description != null && !description.isEmpty()) {
            comment.append(' ').append(description);
        }

        String details = buildCommentDetails(pValue);
        if (details != null) {
            if (comment.length() > 0) {
                comment.append('\n');
            }
            comment.append(' ').append(details);
        }
        return comment.toString();
    }

    private static String buildCommentDetails(ConfigValue<?> pValue) {
        if (pValue instanceof BooleanValue) {
            return "Accepted values: true, false";
        }
        if (pValue instanceof EnumValue<?> enumValue) {
            String acceptedValues = enumValue.getAllowedValues().stream().map(Enum::name).collect(Collectors.joining(","));
            return "Accepted values: " + acceptedValues;
        }
        if (pValue instanceof IntValue || pValue instanceof LongValue || pValue instanceof FloatValue || pValue instanceof DoubleValue) {
            return "Min: " + pValue.getMinValue().orElse(null) + ", Max: " + pValue.getMaxValue().orElse(null) + ", Default: " + pValue.getDefault();
        }
        return null;
    }

    private static Object toWritableValue(Object pRawValue) {
        if (pRawValue instanceof Enum<?> enumValue) {
            return enumValue.name();
        }
        if (pRawValue instanceof Float) {
            return ((Number) pRawValue).doubleValue();
        }
        if (pRawValue instanceof List<?> listValue) {
            return new ArrayList<>(listValue);
        }
        return pRawValue;
    }

    private static void restoreDefaults(ModConfig pSpec) {
        for (ConfigValue<?> value : pSpec.getValues().values()) {
            value.reset();
        }
    }

    private static void readValues(CommentedConfig pConfig, ModConfig pSpec, Path pPath) {
        Set<String> knownKeys = pSpec.getValues().keySet();
        for (String key : pConfig.valueMap().keySet()) {
            if (!knownKeys.contains(key)) {
                LightConfig.LOGGER.warn("Unknown config key '{}' in '{}' (ignored)", key, pPath);
            }
        }

        for (Map.Entry<String, ConfigValue<?>> entry : pSpec.getValues().entrySet()) {
            Object rawValue = pConfig.get(entry.getKey());
            if (rawValue != null) {
                applyValue(entry.getValue(), rawValue, pPath);
            } else {
                LightConfig.LOGGER.info("Missing key '{}' in '{}'; using default '{}'", entry.getKey(), pPath, entry.getValue().getDefault());
                entry.getValue().reset();
            }
        }
    }

    private static void applyValue(ConfigValue<?> pConfigValue, Object pRawValue, Path pPath) {
        try {
            pConfigValue.setParsed(pRawValue);
        } catch (RuntimeException pException) {
            LightConfig.LOGGER.warn("Invalid value '{}' for '{}' in '{}'; using default '{}' ({})", pRawValue, pConfigValue.getKey(), pPath, pConfigValue.getDefault(), pException.getMessage());
            pConfigValue.reset();
        }
    }

    private static Map<String, Object> collectStoredValues(ModConfig pSpec) {
        Map<String, Object> storedValues = new LinkedHashMap<>();
        for (Map.Entry<String, ConfigValue<?>> entry : pSpec.getValues().entrySet()) {
            storedValues.put(entry.getKey(), entry.getValue().getStoredValue());
        }
        return storedValues;
    }

    private static void logChanges(ModConfig pSpec, Map<String, Object> pPreviousValues) {
        for (Map.Entry<String, ConfigValue<?>> entry : pSpec.getValues().entrySet()) {
            String key = entry.getKey();
            Object before = pPreviousValues.get(key);
            Object after = entry.getValue().getStoredValue();
            if (!Objects.equals(before, after)) {
                LightConfig.LOGGER.info("'{}': '{}' changed from '{}' to '{}'", pSpec.getFileName(), key, before, after);
            }
        }
    }

    public static List<String> validate(ModConfig pSpec, Path pPath) {
        List<String> errors = new ArrayList<>();
        try (CommentedFileConfig config = CommentedFileConfig.builder(pPath).build()) {
            config.load();

            Set<String> expectedKeys = pSpec.getValues().keySet();
            Set<String> actualKeys = config.valueMap().keySet();

            for (String key : expectedKeys) {
                if (!actualKeys.contains(key)) {
                    errors.add("missing key '" + key + "'");
                }
            }
            for (String key : actualKeys) {
                if (!expectedKeys.contains(key)) {
                    errors.add("unknown key '" + key + "'");
                }
            }

            for (Map.Entry<String, ConfigValue<?>> entry : pSpec.getValues().entrySet()) {
                Object rawValue = config.get(entry.getKey());
                if (rawValue == null) {
                    continue;
                }
                try {
                    entry.getValue().parse(rawValue);
                } catch (RuntimeException pException) {
                    errors.add("invalid '" + entry.getKey() + "': " + pException.getMessage());
                }
            }
        } catch (Exception pException) {
            errors.add("cannot read TOML: " + pException.getMessage());
        }
        return errors;
    }
}
