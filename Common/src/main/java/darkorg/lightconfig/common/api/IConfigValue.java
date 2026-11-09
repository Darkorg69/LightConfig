package darkorg.lightconfig.common.api;

import java.util.Optional;

public interface IConfigValue<T> {
    String getKey();

    String getComment();

    T get();

    T getDefault();

    default boolean isOverridden() {
        return false;
    }

    void set(T pValue);

    void reset();

    default boolean isDefault() {
        return this.get().equals(this.getDefault());
    }

    default Optional<T> getMinValue() {
        return Optional.empty();
    }

    default Optional<T> getMaxValue() {
        return Optional.empty();
    }
}
