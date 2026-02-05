package io.quill.paper.util.string;

public final class StringParser {
    private StringParser() { }

    private static <T> T parse(String string, T defaultValue, StringConverter<T> converter) {
        if (string == null || string.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return converter.convert(string);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int toInt(String s, int def) {
        return parse(s, def, Integer::parseInt);
    }

    public static float toFloat(String s, float def) {
        return parse(s, def, Float::parseFloat);
    }

    public static double toDouble(String s, double def) {
        return parse(s, def, Double::parseDouble);
    }

    public static boolean toBoolean(String s, boolean def) {
        if (s == null || s.trim().isEmpty()) {
            return def;
        }
        return Boolean.parseBoolean(s);
    }
}