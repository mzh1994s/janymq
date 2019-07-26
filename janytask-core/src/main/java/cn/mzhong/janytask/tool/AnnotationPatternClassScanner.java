package cn.mzhong.janytask.tool;

import cn.mzhong.janytask.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器，先通过注解将当前classpath*下的所有满足条件的类扫描出来，然后通过一些匹配模式进一步筛选出新的类列表
 *
 * @author mzhong
 * @since 2.0.0
 */
public class AnnotationPatternClassScanner {

    // 是否已经扫描过
    private boolean hasScan;

    private Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

    private Set<String> packages = new HashSet<String>();

    private Set<String> locationPatterns = new HashSet<String>();

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    /**
     * 清除当前扫描过的类，重新执行{@link #scan()}方法会执行新的扫描
     *
     * @since 2.0.0
     */
    public void clear() {
        classes.clear();
    }

    /**
     * 添加一个注解到注解列表，执行{@link #scan()}方法则会扫描到带有注解列表的类
     *
     * @param annotations
     * @since 2.0.0
     */
    public void addAnnotation(Class<? extends Annotation>... annotations) {
        Collections.addAll(this.annotations, annotations);
    }

    /**
     * 从注解列表移除一个注解，要获取新的类列表，则需要重新执行{@link #scan()}
     *
     * @param annotations
     * @since 2.0.0
     */
    public void removeAnnotation(Class<? extends Annotation>... annotations) {
        int len = annotations.length;
        for (int i = 0; i < len; i++) {
            this.annotations.remove(annotations[i]);
        }
    }

    private void resolveLocationPattern(){
        Iterator<String> iterator = packages.iterator();
        Set<String> locationPatterns = new HashSet<String>();
        while (iterator.hasNext()){
            String next = iterator.next();
            locationPatterns.add(next.replace(".", "/")
                    .replace("**", "[a-zA-Z0-9_$.]*")
                    .replace("*", "[a-zA-Z0-9_$]*"));
        }
        this.locationPatterns = locationPatterns;
    }

    public void addPackages(String... packages) {
        Collections.addAll(this.packages, packages);
        resolveLocationPattern();
    }

    public void removePackages(String... packages) {
        int len = packages.length;
        for (int i = 0; i < len; i++) {
            this.packages.remove(packages[i]);
        }
        resolveLocationPattern();
    }

    private boolean annotationMatch(Class<?> _class) {
        Iterator<Class<? extends Annotation>> iterator = this.annotations.iterator();
        while (iterator.hasNext()) {
            Class<? extends Annotation> next = iterator.next();
            if (_class.getAnnotation(next) != null) {
                return true;
            }
        }
        return false;
    }

    private boolean packageMatch(Class<?> _class){
        Iterator<String> iterator = this.locationPatterns.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();

        }
        return false;
    }

    /**
     * 扫描jar包中包含注解列表中的注解的类
     *
     * @param jarFile
     */
    private void scanInJar(JarFile jarFile) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class")) {
                System.out.println(name);
//                try {
//                    Class<?> _class = Class.forName(name.substring(0, name.length() - 6));
//                    if (annotationMatch(_class)) {
//                        classes.add(_class);
//                    }
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    /**
     * 扫描classpath下的包含注解列表中的注解的类
     *
     * @param file  resource文件
     * @param start 包的开始
     */
    private void scanInFile(File file, int start) {
        File[] children = file.listFiles();
        if (children != null) {
            int len = children.length;
            for (int i = 0; i < len; i++) {
                File child = children[i];
                if (child.isDirectory()) {
                    scanInFile(child, start);
                } else if (child.getName().endsWith(".class")) {
                    String classpath = child.getAbsolutePath().substring(start);
                    String classFile = classpath.replace(File.separator, ".");
                    String classname = classFile.substring(0, classFile.length() - 6);
                    try {
                        Class<?> _class = Class.forName(classname);
                        if (annotationMatch(_class)) {
                            classes.add(_class);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 传入包名，扫描类
     *
     * @param packages
     * @return
     */
    public Set<Class<?>> scan(String... packages) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        int len = packages.length;
        for (int i = 0; i < len; i++) {
            classes.addAll(scan(packages[i]));
        }
        return classes;
    }

    public Set<Class<?>> scan(String packages) {

        return classes;
    }

    private void scanByPrefix(String scanPrefix) {
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(scanPrefix);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocolUpperCase = url.getProtocol();
                if ("jar".equalsIgnoreCase(protocolUpperCase)) {
                    JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                    scanInJar(urlConnection.getJarFile());
                } else if ("file".equalsIgnoreCase(protocolUpperCase)) {
                    File classpathFile = new File(url.getFile());
                    int classPathStart = url.getFile().length() - 1;
                    scanInFile(classpathFile, classPathStart);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPackagePrefix(String _package) {
        int firstIndex = _package.indexOf('*');
        if (firstIndex != -1) {
            return _package.substring(0, firstIndex - 1);
        }
        return _package;
    }

    private String getSamePrefix(String str1, String str2) {
        int len = Math.min(str1.length(), str2.length());
        int index = 0;
        for (; index < len; index++) {
            if (str1.charAt(index) != str2.charAt(index)) {
                break;
            }
        }
        if (index > 0) {
            return str1.substring(0, index);
        }
        return null;
    }

    private String[] getSamePrefix() {
        if (packages.isEmpty()) {
            return new String[]{"."};
        }
        // 只有一个包的情况
        else if (packages.size() == 1) {
            Iterator<String> iterator = packages.iterator();
            return new String[]{getPackagePrefix(iterator.next())};
        }
        // 多个包的情况
        else {
            // 1、找出所有前缀
            String[] prefixes = new String[0];
            Iterator<String> iterator = packages.iterator();
            while (iterator.hasNext()) {
                prefixes = Arrays.add(prefixes, getPackagePrefix(iterator.next()));
            }
            // 2、取公共前缀
            String[] samePrefixes = new String[]{prefixes[0]};
            int len = prefixes.length;
            for (int i = 1; i < len; i++) {
                int lenJ = samePrefixes.length;
                for (int j = 0; j < lenJ; j++) {
                    String prefix = prefixes[i];
                    String samePrefix = samePrefixes[j];
                    String newSamePrefix = getSamePrefix(prefix, samePrefix);
                    if (newSamePrefix == null) {
                        samePrefixes = Arrays.add(samePrefixes, prefix);
                    } else {
                        samePrefixes[j] = newSamePrefix;
                    }
                }
            }
            return samePrefixes;
        }
    }

    /**
     * 开始扫描，在没有执行过{@link #addAnnotation(Class[])}或者{@link #removeAnnotation(Class[])}
     * 两个方法的前提下，如果已经扫描过，则会放弃扫描.
     *
     * @since 2.0.0
     */
    public Set<Class<?>> scan() {
        String[] samePrefixes = getSamePrefix();
        if (!hasScan) {
            hasScan = true;
            int len = samePrefixes.length;
            for (int i = 0; i < len; i++) {
                System.out.println(samePrefixes[i]);
                scanByPrefix(samePrefixes[i]);
            }
        }
        return classes;
    }
}
