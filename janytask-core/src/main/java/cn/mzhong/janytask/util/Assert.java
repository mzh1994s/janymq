package cn.mzhong.janytask.util;

/**
 * 不用看了，为了减少依赖，仿照{@link org.springframework.util.Assert}写的
 *
 * @author mzhong
 * @date 2019年7月8日
 */
public class Assert {

    public static void notNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
