package cn.mzhong.janymq.config;

public abstract class LineConfig {
    /**
     * 当消费者空闲时，间隔多少时间检测一次消息
     */
    protected long idleInterval = 3000;
    /**
     * 当消费者处理完一条消息后，休息多少时间进行下一次消息处理
     */
    protected long sleepInterval = 0;

    public long getIdleInterval() {
        return idleInterval;
    }

    public void setIdleInterval(long idleInterval) {
        this.idleInterval = idleInterval;
    }

    public long getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    @Override
    public String toString() {
        return "LineConfig{" +
                "idleInterval=" + idleInterval +
                ", sleepInterval=" + sleepInterval +
                '}';
    }
}
