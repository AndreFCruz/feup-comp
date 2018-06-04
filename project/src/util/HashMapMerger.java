package util;

import report.PatternReport;

import java.util.HashMap;
import java.util.Map;

public class HashMapMerger extends HashMap<String, PatternReport> {
    @Override
    public PatternReport put(String key, PatternReport value) {
        if (!containsKey(key)) return super.put(key, value);
        System.err.println("already contains key: " + key);
        return super.put(key, get(key).merge(value));
    }

    @Override
    public void putAll(Map<? extends String, ? extends PatternReport> m) {
        for (Entry<? extends String, ? extends PatternReport> entry : m.entrySet()){
            put(entry.getKey(), entry.getValue());
            System.err.println("key: " + entry.getKey() + " value: " + entry.getValue().reports.get(0).getValue());

        }
    }
}
