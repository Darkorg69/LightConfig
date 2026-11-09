package darkorg.lightconfig.common.api;

import darkorg.lightconfig.common.impl.ConfigValue;
import darkorg.lightconfig.common.impl.value.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigBuilder {
    private final Map<String, ConfigValue<?>> values = new LinkedHashMap<>();

    public Map<String, ConfigValue<?>> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    private void addValue(String pKey, ConfigValue<?> pValue) {
        if (this.values.containsKey(pKey)) {
            throw new IllegalArgumentException("Duplicate config key: " + pKey);
        }
        this.values.put(pKey, pValue);
    }

    public BooleanValue define(String pKey, boolean pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public BooleanValue define(String pKey, boolean pDefault, String pComment) {
        BooleanValue value = new BooleanValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public IntValue define(String pKey, int pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public IntValue define(String pKey, int pDefault, String pComment) {
        IntValue value = new IntValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public IntValue defineInRange(String pKey, int pDefault, int pMin, int pMax) {
        return this.defineInRange(pKey, pDefault, pMin, pMax, null);
    }

    public IntValue defineInRange(String pKey, int pDefault, int pMin, int pMax, String pComment) {
        IntValue value = new IntValue(pKey, pDefault, pMin, pMax, pComment);
        addValue(pKey, value);
        return value;
    }

    public LongValue define(String pKey, long pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public LongValue define(String pKey, long pDefault, String pComment) {
        LongValue value = new LongValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public LongValue defineInRange(String pKey, long pDefault, long pMin, long pMax) {
        return this.defineInRange(pKey, pDefault, pMin, pMax, null);
    }

    public LongValue defineInRange(String pKey, long pDefault, long pMin, long pMax, String pComment) {
        LongValue value = new LongValue(pKey, pDefault, pMin, pMax, pComment);
        addValue(pKey, value);
        return value;
    }

    public FloatValue define(String pKey, float pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public FloatValue define(String pKey, float pDefault, String pComment) {
        FloatValue value = new FloatValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public FloatValue defineInRange(String pKey, float pDefault, float pMin, float pMax) {
        return this.defineInRange(pKey, pDefault, pMin, pMax, null);
    }

    public FloatValue defineInRange(String pKey, float pDefault, float pMin, float pMax, String pComment) {
        FloatValue value = new FloatValue(pKey, pDefault, pMin, pMax, pComment);
        addValue(pKey, value);
        return value;
    }

    public DoubleValue define(String pKey, double pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public DoubleValue define(String pKey, double pDefault, String pComment) {
        DoubleValue value = new DoubleValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public DoubleValue defineInRange(String pKey, double pDefault, double pMin, double pMax) {
        return this.defineInRange(pKey, pDefault, pMin, pMax, null);
    }

    public DoubleValue defineInRange(String pKey, double pDefault, double pMin, double pMax, String pComment) {
        DoubleValue value = new DoubleValue(pKey, pDefault, pMin, pMax, pComment);
        addValue(pKey, value);
        return value;
    }

    public StringValue define(String pKey, String pDefault) {
        return this.define(pKey, pDefault, null);
    }

    public StringValue define(String pKey, String pDefault, String pComment) {
        StringValue value = new StringValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public <E extends Enum<E>> EnumValue<E> define(String pKey, E pDefault) {
        return this.define(pKey, pDefault, (String) null);
    }

    public <E extends Enum<E>> EnumValue<E> define(String pKey, E pDefault, String pComment) {
        E[] constants = pDefault.getDeclaringClass().getEnumConstants();
        EnumValue<E> value = new EnumValue<>(pKey, pDefault, constants, pComment);
        addValue(pKey, value);
        return value;
    }

    public <E extends Enum<E>> EnumValue<E> define(String pKey, E pDefault, E[] pAllowedValues, String pComment) {
        EnumValue<E> value = new EnumValue<>(pKey, pDefault, pAllowedValues, pComment);
        addValue(pKey, value);
        return value;
    }

    public <E extends Enum<E>> EnumValue<E> define(String pKey, E pDefault, E... pAllowedValues) {
        return this.define(pKey, pDefault, pAllowedValues, null);
    }

    public ListStringValue defineList(String pKey, List<String> pDefault, String pComment) {
        ListStringValue value = new ListStringValue(pKey, pDefault, pComment);
        addValue(pKey, value);
        return value;
    }

    public ListStringValue defineList(String pKey, List<String> pDefault) {
        return this.defineList(pKey, pDefault, null);
    }
}
