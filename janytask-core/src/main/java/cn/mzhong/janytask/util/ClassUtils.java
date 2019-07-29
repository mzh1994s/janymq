package cn.mzhong.janytask.util;

import java.util.HashSet;
import java.util.Set;

/**
 * classpath 工具类
 */
public abstract class ClassUtils {

    private ClassUtils() {
    }


    /**
     * 扫描一个类实现的所有接口
     *
     * @param _class
     * @return
     */
    public static Set<Class<?>> getInterfaces(Class<?> _class) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        Class<?> superclass = _class.getSuperclass();
        if (superclass != null) {
            interfaces.addAll(getInterfaces(superclass));
        }
        Class<?>[] classInterfaces = _class.getInterfaces();
        int index = classInterfaces.length;
        while (index-- > 0) {
            interfaces.add(classInterfaces[index]);
        }
        return interfaces;
    }
}
