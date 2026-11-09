package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.Optional;

public final class FloatValue extends ConfigValue<Float> {
    private final float minimumValue;
    private final float maximumValue;

    public FloatValue(String pKey, float pDefault, String pComment) {
        super(pKey, pDefault, pComment);
        this.minimumValue = -Float.MAX_VALUE;
        this.maximumValue = Float.MAX_VALUE;
    }

    public FloatValue(String pKey, float pDefault, float pMin, float pMax, String pComment) {
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
    public void set(Float pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Float value cannot be null for key: " + getKey());
        }
        this.value = clamp(pValue);
    }

    @Override
    public Optional<Float> getMinValue() {
        return Optional.of(this.minimumValue);
    }

    @Override
    public Optional<Float> getMaxValue() {
        return Optional.of(this.maximumValue);
    }

    public float clamp(float pValue) {
        return Math.max(this.minimumValue, Math.min(this.maximumValue, pValue));
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof Number;
    }

    @Override
    protected String expectedRawType() {
        return "a number";
    }

    @Override
    protected Float parseRaw(String pRawValue) {
        float parsed;
        try {
            parsed = Float.parseFloat(pRawValue.trim());
        } catch (NumberFormatException pException) {
            throw new IllegalArgumentException("Invalid float value '" + pRawValue + "' for key: " + getKey());
        }
        if (!Float.isFinite(parsed)) {
            throw new IllegalArgumentException("Float value must be finite for key: " + getKey());
        }
        if (parsed < this.minimumValue || parsed > this.maximumValue) {
            throw new IllegalArgumentException("Float value " + parsed + " is outside the range [" + this.minimumValue + ", " + this.maximumValue + "] for key: " + getKey());
        }
        return parsed;
    }
}
