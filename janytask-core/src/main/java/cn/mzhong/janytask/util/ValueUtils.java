package cn.mzhong.janytask.util;

public class ValueUtils {
    private ValueUtils() {
    }

    public static long uLong(long... values) {
        for (long value : values) {
            if (value >= 0) {
                return value;
            }
        }
        return 0;
    }

    public static int uInt(int... values) {
        for (int value : values) {
            if (value >= 0) {
                return value;
            }
        }
        return 0;
    }

    public static String uNullStr(String... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static String uEmptyStr(String... values) {
        for (String value : values) {
            if (value != null && value.length() > 0) {
                return value;
            }
        }
        return null;
    }

    public static String uBlankStr(String... values) {
        for (String value : values) {
            if (value != null && value.trim().length() > 0) {
                return value;
            }
        }
        return null;
    }
}
