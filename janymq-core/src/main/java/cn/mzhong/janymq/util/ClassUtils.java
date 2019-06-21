package cn.mzhong.janymq.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * classpath 工具类
 */
public class ClassUtils {

    private ClassUtils() {
    }

    public final static String classpath;
    private final static int cutstart;

    static {
        File classpathFile = new File(ClassUtils.class.getClassLoader().getResource("").getFile());
        classpath = classpathFile.getAbsolutePath();
        cutstart = classpath.length() + 1;
    }

    private static Set<Class<?>> scanByPackage(String packagePattern, File file) {
        Set<Class<?>> list = new HashSet<Class<?>>();
        for (File children : file.listFiles()) {
            if (children.isDirectory()) {
                list.addAll(scanByPackage(packagePattern, children));
            } else if (children.getName().endsWith(".class")) {
                String classpath = children.getParent().substring(cutstart);
                String _package = classpath.replace(File.separator, ".");
                if (_package.matches(packagePattern)) {
                    String filename = children.getName();
                    String classname = _package + "." + filename.substring(0, filename.length() - 6);
                    try {
                        list.add(Class.forName(classname));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    /**
     * 通过包名扫描类
     *
     * @param _package
     * @return
     */
    public static Set<Class<?>> scanByPackage(String _package) {
        final URL classpathUrl = ClassUtils.class.getClassLoader().getResource("");
        File classpathFile = new File(classpathUrl.getFile());
        String packagePattern = _package.replace(".", "\\.");
        packagePattern = packagePattern.replaceAll("\\*{2}", ".+");
        packagePattern = packagePattern.replace("*", "\\S+");
        packagePattern = packagePattern + ".*";
        return scanByPackage(packagePattern, classpathFile);
    }

    /**
     * 通过包名、注解扫描类
     *
     * @param _package
     * @param annotations
     * @return
     */
    public static Set<Class<?>> scanByAnnotation(String _package, Class<? extends Annotation>... annotations) {
        Set<Class<?>> list = new HashSet<Class<?>>();
        Set<Class<?>> foundList = scanByPackage(_package);
        for (Class<?> _class : foundList) {
            for (Class<? extends Annotation> annotation : annotations) {
                if (_class.getAnnotation(annotation) != null) {
                    list.add(_class);
                }
            }
        }
        return list;
    }

    /**
     * 扫描一个类实现的所有接口
     *
     * @param _class
     * @return
     */
    public static Set<Class<?>> getInterfaces(Class<?> _class) {
        Set<Class<?>> interfaces = new HashSet<>();
        Class<?> superclass = _class.getSuperclass();
        if (superclass != null) {
            interfaces.addAll(getInterfaces(superclass));
        }
        interfaces.addAll(Arrays.asList(_class.getInterfaces()));
        return interfaces;
    }

    public static void main(String[] args) {
        System.out.println(scanByPackage(""));
    }
}
