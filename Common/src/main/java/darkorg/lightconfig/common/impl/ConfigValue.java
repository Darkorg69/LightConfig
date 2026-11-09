package darkorg.lightconfig.common.impl;

import darkorg.lightconfig.common.api.IConfigValue;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class ConfigValue<T> implements IConfigValue<T> {
    private final String key;
    private final String comment;
    private final T defaultValue;
    protected T value;
    private Supplier<Optional<T>> syncedValue = Optional::empty;

    protected ConfigValue(String pKey, T pDefaultValue, String pComment) {
        this.key = pKey;
        this.defaultValue = pDefaultValue;
        this.comment = pComment;
        this.value = pDefaultValue;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public T get() {
        return this.syncedValue.get().orElse(this.value);
    }

    T getStoredValue() {
        return this.value;
    }

    @Override
    public boolean isOverridden() {
        return this.syncedValue.get().isPresent();
    }

    @Override
    public T getDefault() {
        return this.defaultValue;
    }

    @Override
    public void set(T pValue) {
        this.value = pValue;
    }

    @Override
    public void reset() {
        this.value = this.defaultValue;
    }

    void bindSyncedValue(Supplier<Optional<T>> pSupplier) {
        this.syncedValue = pSupplier;
    }

    protected T parse(Object pRawValue) {
        if (!acceptsRawValue(pRawValue)) {
            throw new IllegalArgumentException("Expected " + expectedRawType() + " but got " + (pRawValue == null ? "null" : pRawValue.getClass().getSimpleName()) + " for key: " + getKey());
        }
        return parseRaw(String.valueOf(pRawValue));
    }

    protected boolean acceptsRawValue(Object pRawValue) {
        return pRawValue != null;
    }

    protected String expectedRawType() {
        return "a value";
    }

    protected abstract T parseRaw(String pRawValue);

    void setParsed(Object pRawValue) {
        this.value = parse(pRawValue);
    }
}
