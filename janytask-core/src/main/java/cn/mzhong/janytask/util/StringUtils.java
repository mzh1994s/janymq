package cn.mzhong.janytask.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
