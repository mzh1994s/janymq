package cn.mzhong.janytask.util;

import java.lang.annotation.Annotation;

@SuppressWarnings("unchecked")
public abstract class AnnotationUtils {
    private AnnotationUtils() {
    }

    /**
     * @param annotation
     * @param name
     * @param <T>
     * @return 如果没有这个注解属性，则返回null
     */
    public static <T> T getAnnotationValue(Annotation annotation, String name) {
        try {
            return (T) annotation.annotationType().getMethod(name).invoke(annotation);
        } catch (Exception e) {
            return null;
        }
    }
}
