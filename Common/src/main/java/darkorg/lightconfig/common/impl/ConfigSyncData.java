package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.api.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ConfigSyncData(String modId, String fileName, Map<String, Object> values) {
    public static ConfigSyncData fromNBT(CompoundTag pTag) {
        String modId = pTag.getString("modId");
        String fileName = pTag.contains("fileName") ? pTag.getString("fileName") : "";
        Map<String, Object> values = new LinkedHashMap<>();

        ListTag list = pTag.getList("values", Tag.TAG_COMPOUND);
        for (int index = 0; index < list.size(); index++) {
            CompoundTag entryTag = list.getCompound(index);
            String key = entryTag.getString("key");
            String type = entryTag.getString("type");

            Object value;
            switch (type) {
                case "boolean" -> value = entryTag.getBoolean("value");
                case "integer" -> value = entryTag.getInt("value");
                case "long" -> value = entryTag.getLong("value");
                case "double" -> value = entryTag.getDouble("value");
                case "float" -> value = (float) entryTag.getDouble("value");
                case "string", "enum" -> value = entryTag.getString("value");
                case "list_string" -> {
                    ListTag stringList = entryTag.getList("value", Tag.TAG_STRING);
                    List<String> parsedList = new ArrayList<>();
                    for (int i = 0; i < stringList.size(); i++) {
                        parsedList.add(stringList.getString(i));
                    }
                    value = parsedList;
                }
                default -> value = null;
            }

            if (value != null) {
                values.put(key, value);
            }
        }

        return new ConfigSyncData(modId, fileName, values);
    }

    public static ConfigSyncData fromSpec(ModConfig pSpec) {
        return new ConfigSyncData(pSpec.getModId(), pSpec.getFileName(), pSpec.collectAllValues());
    }

    public CompoundTag toNBT() {
        CompoundTag root = new CompoundTag();
        root.putString("modId", this.modId);
        root.putString("fileName", this.fileName);

        ListTag list = new ListTag();
        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("key", entry.getKey());

            Object value = entry.getValue();
            if (value instanceof Boolean booleanValue) {
                entryTag.putBoolean("value", booleanValue);
                entryTag.putString("type", "boolean");
            } else if (value instanceof Integer integerValue) {
                entryTag.putInt("value", integerValue);
                entryTag.putString("type", "integer");
            } else if (value instanceof Long longValue) {
                entryTag.putLong("value", longValue);
                entryTag.putString("type", "long");
            } else if (value instanceof Double doubleValue) {
                entryTag.putDouble("value", doubleValue);
                entryTag.putString("type", "double");
            } else if (value instanceof Float floatValue) {
                entryTag.putDouble("value", floatValue);
                entryTag.putString("type", "float");
            } else if (value instanceof Enum<?> enumValue) {
                entryTag.putString("value", enumValue.name());
                entryTag.putString("type", "enum");
            } else if (value instanceof List<?> listValue) {
                ListTag stringList = new ListTag();
                for (Object element : listValue) {
                    stringList.add(StringTag.valueOf(element.toString()));
                }
                entryTag.put("value", stringList);
                entryTag.putString("type", "list_string");
            } else if (value instanceof String stringValue) {
                entryTag.putString("value", stringValue);
                entryTag.putString("type", "string");
            }

            list.add(entryTag);
        }
        root.put("values", list);

        return root;
    }
}
