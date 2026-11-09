package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.Arrays;
import java.util.List;

public final class EnumValue<E extends Enum<E>> extends ConfigValue<E> {
    private final Class<E> enumClass;
    private final E[] allowedValues;

    public EnumValue(String pKey, E pDefault, E[] pAllowedValues, String pComment) {
        super(pKey, pDefault, pComment);
        this.enumClass = pDefault.getDeclaringClass();
        this.allowedValues = pAllowedValues.length > 0 ? pAllowedValues : this.enumClass.getEnumConstants();
    }

    public Class<E> getEnumClass() {
        return this.enumClass;
    }

    public List<E> getAllowedValues() {
        return Arrays.asList(this.allowedValues);
    }

    public void next() {
        int currentIndex = indexOf(this.value);
        int nextIndex = (currentIndex + 1) % this.allowedValues.length;
        this.value = this.allowedValues[nextIndex];
    }

    public void previous() {
        int currentIndex = indexOf(this.value);
        int previousIndex = (currentIndex - 1 + this.allowedValues.length) % this.allowedValues.length;
        this.value = this.allowedValues[previousIndex];
    }

    @Override
    public void set(E pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("Enum value cannot be null for key: " + getKey());
        }
        if (indexOf(pValue) < 0) {
            throw new IllegalArgumentException("Value '" + pValue.name() + "' is not in allowed values for key: " + getKey());
        }
        this.value = pValue;
    }

    @Override
    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue instanceof String;
    }

    @Override
    protected String expectedRawType() {
        return "an enum name";
    }

    @Override
    protected E parseRaw(String pRawValue) {
        for (E allowedValue : this.allowedValues) {
            if (allowedValue.name().equals(pRawValue)) {
                return allowedValue;
            }
        }
        throw new IllegalArgumentException("Unknown enum value '" + pRawValue + "' for key: " + getKey() + ". Allowed: " + Arrays.toString(this.allowedValues));
    }

    private int indexOf(E pValue) {
        for (int index = 0; index < this.allowedValues.length; index++) {
            if (this.allowedValues[index] == pValue) {
                return index;
            }
        }
        return -1;
    }
}
