package cn.mzhong.janytask.queue.pipleline;

import java.lang.annotation.*;

/**
 * 流水线注解。标识一个方法处理流水线
 *
 * @since 1.0.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pipleline {
    /**
     * 列表名称，默认为接口全名 + 方法名
     *
     * @since 1.0.0
     */
    String value() default "";

    /**
     * 列表版本号，参数级修改更新时使用，默认版本号为default
     *
     * @since 1.0.0
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
     * 空闲时每次检测延时时间
     *
     * @return
     * @since 1.0.0
     */
    long idleInterval() default -1;

    /**
     * 每次任务完成后延时时间
     *
     * @return
     * @since 1.0.0
     */
    long sleepInterval() default 0;
}
