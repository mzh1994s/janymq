package cn.mzhong.janytask.util;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;

@SuppressWarnings("unchecked")
public abstract class PropertiesUtils {

    private PropertiesUtils() {
    }

    public static Map<String, String> getMergedProperties(String classpathFile) {
        Map<String, String> mergedProperties = new HashMap<String, String>();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(classpathFile);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if ("jar".equalsIgnoreCase(protocol)) {
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    Enumeration<JarEntry> entries = connection.getJarFile().entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        // 未处理这种情况
                        System.out.println(jarEntry.getName());
                    }
                } else if ("file".equalsIgnoreCase(protocol)) {
                    Properties properties = new Properties();
                    properties.load(url.openStream());
                    Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
                    while (enumeration.hasMoreElements()) {
                        String name = enumeration.nextElement();
                        mergedProperties.put(name, properties.getProperty(name));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mergedProperties;
    }
}
