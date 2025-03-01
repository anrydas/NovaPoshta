package das.tools.np.gui.controllers.search.options;

import java.util.HashMap;
import java.util.Map;

public enum SearchCondition {
    EQUAL, NOT_EQUAL, GREATER, LOWER, CONTAINS;

    private static final Map<SearchCondition,String> stringNames = new HashMap<>();
    static {
        stringNames.put(EQUAL, "=");
        stringNames.put(NOT_EQUAL, "!=");
        stringNames.put(GREATER, ">");
        stringNames.put(LOWER, "<");
        stringNames.put(CONTAINS, "~");
    }
    public static SearchCondition fromString(String s) {
        for (Map.Entry<SearchCondition,String> entry: stringNames.entrySet()) {
            if (entry.getValue().equals(s)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static String stringValue(SearchCondition c) {
        return stringNames.get(c);
    }
}
