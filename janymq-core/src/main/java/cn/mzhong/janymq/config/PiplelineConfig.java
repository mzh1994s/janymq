package cn.mzhong.janymq.config;

public class PiplelineConfig extends LineConfig{

    @Override
    public String toString() {
        return "PiplelineConfig{" +
                "idleInterval=" + idleInterval +
                ", sleepInterval=" + sleepInterval +
                '}';
    }
}
