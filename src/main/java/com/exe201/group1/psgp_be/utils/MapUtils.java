package com.exe201.group1.psgp_be.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MapUtils {

    public static Map<String, Object> build(List<String> keys, List<Object> values) {
        if (keys.size() != values.size()) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i), values.get(i));
        }

        return result;
    }

    public static Map<String, Object> checkIfObjectIsMap(Object object) {
        return object instanceof Map ? (Map<String, Object>) object : null;
    }

    public static Map<String, Object> getMapFromObject(Object target, String... keys) {
        Map<String, Object> result = new HashMap<>((Map<String, Object>) target);

        for (String key : keys) {
            result = extractMap(result, key);
        }

        return result;
    }

    public static List<Map<String, Object>> getMapListFromObject(Object target, String key) {
        Map<String, Object> result = getMapFromObject(target);

        return (List<Map<String, Object>>) result.get(key);
    }

    private static Map<String, Object> extractMap(Map<String, Object> target, String key) {
        return (Map<String, Object>) target.get(key);
    }
}
