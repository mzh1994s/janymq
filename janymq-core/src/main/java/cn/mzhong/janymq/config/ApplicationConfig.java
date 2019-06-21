package cn.mzhong.janymq.config;

public class ApplicationConfig {

    /**
     * 扫描消费者、提供者的包，支持通配符“*”，
     * cn.mzhong.*.util 可以匹配到cn.mzhong.a.util...和cn.mzhong.b.util...等，
     * cn.mzhong.**.util 可以匹配到cn.mzhong.a.util...和cn.mzhong.a.b.util...等，
     * 默认情况下JSimpleMQ应用程序会扫描当前classpath下的所有包。
     */
    protected String basePackage = "";

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
