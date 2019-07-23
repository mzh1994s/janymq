package cn.mzhong.janytask.schedule;

import java.lang.annotation.*;

/**
 * 定时任务时间表达式
 *
 * @since 2.0.0
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    /**
     * 定时任务cron
     *
     * @return
     */
    String cron();

    /**
     * 时区
     *
     * @return
     */
    String zone() default "";
}
