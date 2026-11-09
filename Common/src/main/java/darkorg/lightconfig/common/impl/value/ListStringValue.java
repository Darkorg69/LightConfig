package darkorg.lightconfig.common.impl.value;

import darkorg.lightconfig.common.impl.ConfigValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListStringValue extends ConfigValue<List<String>> {

    public ListStringValue(String pKey, List<String> pDefault, String pComment) {
        super(pKey, new ArrayList<>(pDefault), pComment);

        this.value = new ArrayList<>(pDefault);
    }

    private static List<String> parseBracketedList(String pRawValue) {
        String inner = pRawValue.substring(1, pRawValue.length() - 1).trim();
        if (inner.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> parsed = new ArrayList<>();
        for (String part : inner.split(",")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
            }
            parsed.add(trimmed);
        }
        return parsed;
    }

    @Override
    public void set(List<String> pValue) {
        if (pValue == null) {
            throw new IllegalArgumentException("List value cannot be null for key: " + getKey());
        }
        this.value = new ArrayList<>(pValue);
    }

    public void add(String pValue) {
        if (pValue != null && !this.value.contains(pValue)) {
            this.value.add(pValue);
        }
    }

    public boolean remove(String pValue) {
        return this.value.remove(pValue);
    }

    public boolean contains(String pValue) {
        return this.value.contains(pValue);
    }

    public List<String> getValues() {
        return Collections.unmodifiableList(this.value);
    }

    public int size() {
        return this.value.size();
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public void clear() {
        this.value.clear();
    }

    @Override
    public boolean isDefault() {
        return this.getDefault().equals(this.value);
    }

    @Override
    public void reset() {
        this.value = new ArrayList<>(this.getDefault());
    }

    @Override
    protected List<String> parse(Object pRawValue) {
        if (!(pRawValue instanceof List<?> rawList)) {
            throw new IllegalArgumentException("Expected a list but got "
                    + (pRawValue == null ? "null" : pRawValue.getClass().getSimpleName()) + " for key: " + getKey());
        }
        List<String> parsed = new ArrayList<>();
        for (Object element : rawList) {
            parsed.add(String.valueOf(element));
        }
        return parsed;
    }

    @Override
    protected List<String> parseRaw(String pRawValue) {
        if (pRawValue.startsWith("[") && pRawValue.endsWith("]")) {
            return parseBracketedList(pRawValue);
        }
        List<String> parsed = new ArrayList<>();
        parsed.add(pRawValue);
        return parsed;
    }
}
