package cn.mzhong.janytask.config;

public class QueueConfig {


    /**
     * 计划任务（只用于表述时间范围）
     *
     * @return
     * @since 1.0.1
     */
    private String cron = "* * * * * ?";

    /**
     * 计划任务（时间区间）
     *
     * @return
     * @since 1.0.1
     */
    private String zone = "";

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return "QueueConfig{" +
                "cron='" + cron + '\'' +
                ", zone='" + zone + '\'' +
                '}';
    }
}
