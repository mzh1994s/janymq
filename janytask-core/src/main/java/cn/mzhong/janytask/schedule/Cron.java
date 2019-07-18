package cn.mzhong.janytask.schedule;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务时间表达式
 *
 * @since 1.0.1
 */
public @interface Cron {

    /**
     * 定时任务cron
     *
     * @return
     */
    String value();

    /**
     * 时区
     *
     * @return
     */
    String zone();
}
