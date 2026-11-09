package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.Optional;

public final class IntValue extends ConfigValue<Integer> {
    private final int minimumValue;
    private final int maximumValue;

    public IntValue(String pKey, int pDefault, String pComment) {
        super(pKey, pDefault, pComment);
        this.minimumValue = Integer.MIN_VALUE;
        this.maximumValue = Integer.MAX_VALUE;
    }

    public IntValue(String pKey, int pDefault, int pMin, int pMax, String pComment) {
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
    public void set(Integer pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Integer value cannot be null for key: " + getKey());
        }
        this.value = clamp(pValue);
    }

    @Override
    public Optional<Integer> getMinValue() {
        return Optional.of(this.minimumValue);
    }

    @Override
    public Optional<Integer> getMaxValue() {
        return Optional.of(this.maximumValue);
    }

    public int clamp(int pValue) {
        return Math.max(this.minimumValue, Math.min(this.maximumValue, pValue));
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof Integer || pRawValue instanceof Long;
    }

    @Override
    protected String expectedRawType() {
        return "an integer";
    }

    @Override
    protected Integer parseRaw(String pRawValue) {
        int parsed;
        try {
            parsed = Integer.parseInt(pRawValue.trim());
        } catch (NumberFormatException pException) {
            throw new IllegalArgumentException("Invalid integer value '" + pRawValue + "' for key: " + getKey());
        }
        if (parsed < this.minimumValue || parsed > this.maximumValue) {
            throw new IllegalArgumentException("Integer value " + parsed + " is outside the range [" + this.minimumValue + ", " + this.maximumValue + "] for key: " + getKey());
        }
        return parsed;
    }
}
