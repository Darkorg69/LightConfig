package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.Optional;

public final class LongValue extends ConfigValue<Long> {
    private final long minimumValue;
    private final long maximumValue;

    public LongValue(String pKey, long pDefault, String pComment) {
        super(pKey, pDefault, pComment);
        this.minimumValue = Long.MIN_VALUE;
        this.maximumValue = Long.MAX_VALUE;
    }

    public LongValue(String pKey, long pDefault, long pMin, long pMax, String pComment) {
        super(pKey, pDefault, pComment);
        if (pMin > pMax) {
            throw new IllegalArgumentException("Minimum value (" + pMin + ") cannot be greater than maximum value (" + pMax + ") for key: " + pKey);
        }
        if (pDefault < pMin || pDefault > pMax) {
            throw new IllegalArgumentException("Default value (" + pDefault + ") must be between min (" + pMin + ") and max (" + pMax + ") for key: " + pKey);
        }
        this.minimumValue = pMin;
        this.maximumValue = pMax;
    }

    @Override
    public void set(Long pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Long value cannot be null for key: " + getKey());
        }
        this.value = clamp(pValue);
    }

    @Override
    public Optional<Long> getMinValue() {
        return Optional.of(this.minimumValue);
    }

    @Override
    public Optional<Long> getMaxValue() {
        return Optional.of(this.maximumValue);
    }

    public long clamp(long pValue) {
        return Math.max(this.minimumValue, Math.min(this.maximumValue, pValue));
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof Integer || pRawValue instanceof Long;
    }

    @Override
    protected String expectedRawType() {
        return "a long";
    }

    @Override
    protected Long parseRaw(String pRawValue) {
        long parsed;
        try {
            parsed = Long.parseLong(pRawValue.trim());
        } catch (NumberFormatException pException) {
            throw new IllegalArgumentException("Invalid long value '" + pRawValue + "' for key: " + getKey());
        }
        if (parsed < this.minimumValue || parsed > this.maximumValue) {
            throw new IllegalArgumentException("Long value " + parsed + " is outside the range [" + this.minimumValue + ", " + this.maximumValue + "] for key: " + getKey());
        }
        return parsed;
    }
}
