package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

public final class BooleanValue extends ConfigValue<Boolean> {
    public BooleanValue(String pKey, boolean pDefaultValue, String pComment) {
        super(pKey, pDefaultValue, pComment);
    }

    @Override
    public void set(Boolean pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Boolean value cannot be null for key: " + getKey());
        }
        this.value = pValue;
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof Boolean;
    }

    @Override
    protected String expectedRawType() {
        return "a boolean";
    }

    @Override
    protected Boolean parseRaw(String pRawValue) {
        String normalized = pRawValue.trim();
        if ("true".equalsIgnoreCase(normalized)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(normalized)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + pRawValue + "' for key: " + getKey());
    }
}
