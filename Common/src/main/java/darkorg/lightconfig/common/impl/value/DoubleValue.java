package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.Optional;

public final class DoubleValue extends ConfigValue<Double> {
    private final double minimumValue;
    private final double maximumValue;

    public DoubleValue(String pKey, double pDefault, String pComment) {
        super(pKey, pDefault, pComment);
        this.minimumValue = -Double.MAX_VALUE;
        this.maximumValue = Double.MAX_VALUE;
    }

    public DoubleValue(String pKey, double pDefault, double pMin, double pMax, String pComment) {
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
    public void set(Double pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Double value cannot be null for key: " + getKey());
        }
        this.value = clamp(pValue);
    }

    @Override
    public Optional<Double> getMinValue() {
        return Optional.of(this.minimumValue);
    }

    @Override
    public Optional<Double> getMaxValue() {
        return Optional.of(this.maximumValue);
    }

    public double clamp(double pValue) {
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
    protected Double parseRaw(String pRawValue) {
        double parsed;
        try {
            parsed = Double.parseDouble(pRawValue.trim());
        } catch (NumberFormatException pException) {
            throw new IllegalArgumentException("Invalid double value '" + pRawValue + "' for key: " + getKey());
        }
        if (!Double.isFinite(parsed)) {
            throw new IllegalArgumentException("Double value must be finite for key: " + getKey());
        }
        if (parsed < this.minimumValue || parsed > this.maximumValue) {
            throw new IllegalArgumentException("Double value " + parsed + " is outside the range [" + this.minimumValue + ", " + this.maximumValue + "] for key: " + getKey());
        }
        return parsed;
    }
}
