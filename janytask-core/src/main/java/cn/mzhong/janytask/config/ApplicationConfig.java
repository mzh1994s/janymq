package cn.mzhong.janytask.config;

public class ApplicationConfig {

    protected String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ApplicationConfig{" +
                "name='" + name + '\'' +
                '}';
    }
}
