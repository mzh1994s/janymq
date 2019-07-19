package cn.mzhong.janytask.config;

public class QueueConfig {
    /**
     * 当消费者检测到任务列表为空时的空闲时间
     */
    protected long idleInterval = 0;
    /**
     * 当消费者处理完一条消息后的休息时间
     */
    protected long sleepInterval = 0;

    protected String cron = "";

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
        return "QueueConfig{" +
                "idleInterval=" + idleInterval +
                ", sleepInterval=" + sleepInterval +
                '}';
    }
}
