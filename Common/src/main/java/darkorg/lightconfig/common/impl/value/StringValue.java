package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

public final class StringValue extends ConfigValue<String> {
    public StringValue(String pKey, String pDefault, String pComment) {
        super(pKey, pDefault, pComment);
    }

    @Override
    public void set(String pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("String value cannot be null for key: " + getKey());
        }
        this.value = pValue;
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof String;
    }

    @Override
    protected String expectedRawType() {
        return "a string";
    }

    @Override
    protected String parseRaw(String pRawValue) {
        return pRawValue;
    }
}
