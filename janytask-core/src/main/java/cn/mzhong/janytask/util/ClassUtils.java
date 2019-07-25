package cn.mzhong.janytask.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * classpath 工具类
 */
public abstract class ClassUtils {

    private ClassUtils() {
    }

    /**
     * 扫描本地classpath中的类
     *
     * @param packagePattern
     * @param file
     * @param classPathStart
     * @return
     */
    private static Set<Class<?>> scanByPackage(String packagePattern, File file, int classPathStart) {
        Set<Class<?>> list = new HashSet<Class<?>>();
        for (File children : file.listFiles()) {
            if (children.isDirectory()) {
                list.addAll(scanByPackage(packagePattern, children, classPathStart));
            } else if (children.getName().endsWith(".class")) {
                String classpath = children.getAbsolutePath().substring(classPathStart);
                String classFile = classpath.replace(File.separator, ".");
                if (classFile.matches(packagePattern)) {
                    String classname = classFile.substring(0, classFile.length() - 6);
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
     * 扫描jar包中的类
     *
     * @param packagePattern
     * @param jarFile
     * @return
     */
    private static Set<Class<?>> scanByPackage(String packagePattern, JarFile jarFile) {
        Set<Class<?>> list = new HashSet<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class") && name.matches(packagePattern)) {
                try {
                    list.add(Class.forName(name.substring(0, name.length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
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
        String packagePattern = _package.replace(".", "\\.");
        packagePattern = packagePattern.replaceAll("\\*{2}", ".+");
        packagePattern = packagePattern.replace("*", "\\S+");
        packagePattern = packagePattern + ".*";
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocolUpperCase = url.getProtocol().toUpperCase();
                if ("jar".equalsIgnoreCase(protocolUpperCase)) {
                    JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                    classSet.addAll(scanByPackage(packagePattern, urlConnection.getJarFile()));
                } else if ("file".equalsIgnoreCase(protocolUpperCase)) {
                    File classpathFile = new File(url.getFile());
                    int classPathStart = url.getFile().length() - 1;
                    classSet.addAll(scanByPackage(packagePattern, classpathFile, classPathStart));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static Set<Class<?>> scanByPackage(JarFile jarFile, Class<? extends Annotation>... annotations) {
        Set<Class<?>> list = new HashSet<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class") && name.matches(packagePattern)) {
                try {
                    list.add(Class.forName(name.substring(0, name.length() - 6)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static Set<Class<?>> scanByAnnotation(Class<? extends Annotation>... annotations){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocolUpperCase = url.getProtocol().toUpperCase();
                if ("jar".equalsIgnoreCase(protocolUpperCase)) {
                    JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                    urlConnection.getJarFile().entries();
                } else if ("file".equalsIgnoreCase(protocolUpperCase)) {
                    File classpathFile = new File(url.getFile());
                    int classPathStart = url.getFile().length() - 1;
                    classSet.addAll(scanByPackage(packagePattern, classpathFile, classPathStart));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return classSet;
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
        Iterator<Class<?>> iterator = foundList.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = iterator.next();
            int len = annotations.length;
            for (int i = 0; i < len; i++) {
                if (_class.getAnnotation(annotations[i]) != null) {
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
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        Class<?> superclass = _class.getSuperclass();
        if (superclass != null) {
            interfaces.addAll(getInterfaces(superclass));
        }
        Class<?>[] classInterfaces = _class.getInterfaces();
        int len = classInterfaces.length;
        for (int i = 0; i < len; i++) {
            interfaces.add(classInterfaces[i]);
        }
        return interfaces;
    }

    public static void main(String[] args) {
        System.out.println(scanByPackage(""));
    }
}
