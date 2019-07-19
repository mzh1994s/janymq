package cn.mzhong.janytask.queue.loopline;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loopline {
    /**
     * 列表名称，默认为接口全名 + 方法名
     */
    String value() default "";

    /**
     * 列表版本号，参数级修改更新时使用，默认版本号为default
     */
    String version() default "";

    /**
     * 计划任务（只用于表述时间范围）
     *
     * @return
     * @since 1.0.1
     */
    String cron() default "* * * * * ?";

    /**
     * 计划任务（时间区间）
     *
     * @return
     * @since 1.0.1
     */
    String zone() default "";

    /**
     * 空闲时每次检测延时时间
     *
     * @return
     */
    long idleInterval() default -1;

    /**
     * 每次任务完成后延时时间
     *
     * @return
     */
    long sleepInterval() default 0;
}
