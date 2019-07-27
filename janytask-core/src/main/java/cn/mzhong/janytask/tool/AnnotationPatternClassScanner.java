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
 * 类扫描器，先通过预定义注解、预定义包将当前classpath*下的所有满足条件的类扫描出来，然后通过一些匹配模式进一步筛选出新的类列表
 * <p>
 * <i>非线程安全</i>
 * <p/>
 *
 * @author mzhong
 * @since 2.0.0
 */
public class AnnotationPatternClassScanner {

    /**
     * 预备注解列表，当执行{@link #scan()}时，会将此列表中的注解修饰的类扫描出来。如果注解列表为空，则不会扫描到任何类
     *
     * @since 2.0.0
     */
    final private Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

    /**
     * 预备包列表（已转换为类名正则匹配器），当执行{@link #scan()}时，会将类名与此列表中的匹配器匹配，满足任何一个匹配器时，类就会被收集。
     * 如果此列表为空，则会默认扫描当前classpath（不会扫描到jar）中的所有类。
     *
     * @since 2.0.0
     */
    final private Set<String> classPatterns = new HashSet<String>();

    /**
     * 保存每次执行{@link #scan()}扫描出来的符合扫描条件的类。
     */
    final private Set<String> resources = new HashSet<String>();

    /**
     * 清除当前扫描过的类列表{@link #resources}
     *
     * @since 2.0.0
     */
    public void clear() {
        resources.clear();
    }

    /**
     * 添加一个注解到注解列表，执行{@link #select()}方法则会扫描到带有注解列表的类
     *
     * @param annotations
     * @since 2.0.0
     */
    public void addAnnotation(Class<? extends Annotation>... annotations) {
        Collections.addAll(this.annotations, annotations);
    }

    /**
     * 从注解列表移除一个注解，要获取新的类列表，则需要重新执行{@link #select()}
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

    /**
     * 将给定的包名（支持通配符*、**）转换为class匹配正则表达式
     *
     * @param name
     * @return
     * @since 2.0.0
     */
    private String coverToClassPattern(String name) {
        return name.replace("**", "[a-zA-Z0-9_$.]*")
                .replace("*", "[a-zA-Z0-9_$]*");
    }

    /**
     * 添加预扫描包
     *
     * @param packages
     * @since 2.0.0
     */
    public void addPackages(String... packages) {
        int index = packages.length;
        while (index-- != 0) {
            this.classPatterns.add(coverToClassPattern(packages[index]));
        }
    }

    /**
     * 删除预扫描包
     *
     * @param packages
     * @since 2.0.0
     */
    public void removePackages(String... packages) {
        int index = packages.length;
        while (index-- != 0) {
            this.classPatterns.remove(coverToClassPattern(packages[index]));
        }
    }

    /**
     * 判断类是拥有预定义注解中任何一个
     *
     * @param _class
     * @return
     * @since 2.0.0
     */
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

    /**
     * 判断类是拥有给定的注解数组中任何一个
     *
     * @param _class
     * @param annotations
     * @return
     * @since 2.0.0
     */
    private boolean annotationMatch(Class<?> _class, Class<? extends Annotation>[] annotations) {
        int index = annotations.length;
        while (index-- != 0) {
            if (_class.getAnnotation(annotations[index]) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断类名和classPattern是否匹配
     *
     * @param classname
     * @param classPattren
     * @return
     * @since 2.0.0
     */
    private boolean packageMatch(String classname, String classPattren) {
        return classname.matches(classPattren + "[a-zA-Z0-9_$.]*");
    }

    /**
     * 判断类名和classPattern列表中的任何一个是否匹配
     *
     * @param classname
     * @return
     * @since 2.0.0
     */
    private boolean packageMatch(String classname) {
        if (this.classPatterns.isEmpty()) {
            return true;
        }
        Iterator<String> iterator = this.classPatterns.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (packageMatch(classname, next)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断类名和包数组中的任何一个是否匹配
     *
     * @param classname
     * @param packages
     * @return
     * @since 2.0.0
     */
    private boolean packageMatch(String classname, String[] packages) {
        int index = packages.length;
        if (index == 0) {
            return true;
        }
        while (index-- > 0) {
            if (packageMatch(classname, coverToClassPattern(packages[index]))) {
                return true;
            }
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
                String classname = name.replace('/', '.').substring(0, name.length() - 6);
                if (packageMatch(classname)) {
                    resources.add(classname);
                }
            }
        }
    }

    /**
     * 扫描classpath下的包含注解列表中的注解的类
     *
     * @param file  resource文件
     * @param packageStart 包的开始
     */
    private void scanInFile(File file, int packageStart) {
        File[] children = file.listFiles();
        if (children != null) {
            int index = children.length;
            while (index-- != 0) {
                File child = children[index];
                if (child.isDirectory()) {
                    scanInFile(child, packageStart);
                } else if (child.getName().endsWith(".class")) {
                    String name = child.getAbsolutePath().substring(packageStart);
                    String classname = name.replace(File.separator, ".").substring(0, name.length() - 6);
                    if (packageMatch(classname)) {
                        resources.add(classname);
                    }
                }
            }
        }
    }

    /**
     * 通过包前部分去扫描类，包括Jar中的类和classpath目录下的类
     *
     * @param packageHead
     * @since 2.0.0
     */
    private void scanByPackageHead(String packageHead) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = loader.getResources(packageHead.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocolUpperCase = url.getProtocol();
                if ("jar".equalsIgnoreCase(protocolUpperCase)) {
                    JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                    scanInJar(urlConnection.getJarFile());
                } else if ("file".equalsIgnoreCase(protocolUpperCase)) {
                    File classpathFile = new File(url.getFile());
                    String filepath = classpathFile.getAbsolutePath();
                    // 计算包开始的偏移量
                    int packageRel = packageHead.length() == 0 ? -1 : packageHead.length();
                    int packageStart = filepath.length() - packageRel;
                    scanInFile(classpathFile, packageStart);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取传入的包前部分（*之前的部分）
     *
     * @param _package
     * @return
     */
    private String getPackageHead(String _package) {
        int firstIndex = _package.indexOf('*');
        if (firstIndex != -1) {
            return _package.substring(0, firstIndex - 1);
        }
        return _package;
    }

    /**
     * 计算两个包相同的包部分(末尾不含“.”)
     * <p>
     * 包：cn.mzhong.janytask.tconsumer<br/>
     * 包：cn.mzhong.janytask.tproducer<br/>
     * 相同的部分计算后是 cn.mzhong.janytask
     * <p/>
     *
     * @param package1 包1
     * @param package2 包2
     * @return 两个包相同包部分
     */
    private String getSamePackageHead(String package1, String package2) {
        int index = 0;
        String varPackage1 = package1.charAt(package1.length() - 1) == '.' ? package1 : package1 + ".";
        String varPackage2 = package2.charAt(package2.length() - 1) == '.' ? package2 : package2 + ".";
        do {
            int indexof1 = varPackage1.indexOf('.');
            int indexof2 = varPackage2.indexOf('.');
            if (indexof1 == -1 || indexof2 == -1 || indexof1 != indexof2
                    || !varPackage1.substring(0, indexof1).equalsIgnoreCase(varPackage2.substring(0, indexof2))) {
                break;
            }
            index = indexof1 + index + 1;
            varPackage1 = varPackage1.substring(indexof1 + 1);
            varPackage2 = varPackage2.substring(indexof2 + 1);
        } while (true);
        if (index > 0) {
            return package1.substring(0, index - 1);
        }
        return null;
    }

    /**
     * 计算包列表的公共部分
     *
     * <p>
     * 包：cn.mzhong.janytask.tconsumer<br/>
     * 包：cn.mzhong.janytask.tproducer<br/>
     * 包：org.mzhong.janytask.tproducer<br/>
     * 相同的部分计算后是 [cn.mzhong.janytask,org.mzhong.janytask.tproducer]
     * <p/>
     *
     * @return
     * @since 2.0.0
     */
    private String[] getSamePackageHead() {
        if (classPatterns.isEmpty()) {
            return new String[]{""};
        }
        // 只有一个包的情况
        else if (classPatterns.size() == 1) {
            Iterator<String> iterator = classPatterns.iterator();
            return new String[]{getPackageHead(iterator.next())};
        }
        // 多个包的情况
        else {
            // 1、找出所有前部分
            String[] packageHeads = new String[0];
            Iterator<String> iterator = classPatterns.iterator();
            while (iterator.hasNext()) {
                packageHeads = Arrays.add(packageHeads, getPackageHead(iterator.next()));
            }
            // 2、取公共前部分
            String[] samePackageHeads = new String[]{packageHeads[0]};
            int indexP = packageHeads.length;
            while (indexP-- != 0) {
                String prefix = packageHeads[indexP];
                // 用于标识有没有扫描到公共前部分
                boolean hasSamePrefix = false;
                int indexS = samePackageHeads.length;
                while (indexS-- != 0) {
                    String samePrefix = samePackageHeads[indexS];
                    String newSamePrefix = getSamePackageHead(prefix, samePrefix);
                    // 如果有公共前部分，则替换为新的前部分
                    if (newSamePrefix != null) {
                        samePackageHeads[indexS] = newSamePrefix;
                        hasSamePrefix = true;
                    }
                }
                // 如果没有公共前部分，则是一个新的前部分
                if (!hasSamePrefix) {
                    samePackageHeads = Arrays.add(samePackageHeads, prefix);
                }
            }
            return samePackageHeads;
        }
    }

    /**
     * 执行扫描操作，会清除原本扫描到的类
     *
     * @since 2.0.0
     */
    public void scan() {
        this.clear();
        String[] packageHeads = getSamePackageHead();
        int index = packageHeads.length;
        while (index-- != 0) {
            scanByPackageHead(packageHeads[index]);
        }
    }

    /**
     * 选择扫描到的所有类，必须先进行扫描操作{@link #scan()}
     *
     * @return
     * @throws ClassNotFoundException
     */
    public Set<Class<?>> select() throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Iterator<String> iterator = resources.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = Class.forName(iterator.next(), false, this.getClass().getClassLoader());
            if (annotationMatch(_class)) {
                classes.add(_class);
            }
        }
        return classes;
    }

    /**
     * 选择扫描到的符合指定包名和指定注解的类，必须先进行扫描操作{@link #scan()}
     *
     * @param packages
     * @return
     */
    public Set<Class<?>> select(String[] packages, Class<? extends Annotation>[] annotations) throws ClassNotFoundException {
        Set<Class<?>> classes = select();
        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = iterator.next();
            if (!(annotationMatch(_class, annotations) && packageMatch(_class.getName(), packages))) {
                iterator.remove();
            }
        }
        return classes;
    }

    public Set<Class<?>> select(String[] packages) throws ClassNotFoundException {
        Set<Class<?>> classes = select();
        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class<?> _class = iterator.next();
            if (!(annotationMatch(_class) && packageMatch(_class.getName(), packages))) {
                iterator.remove();
            }
        }
        return classes;
    }
}
