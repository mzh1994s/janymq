package cn.mzhong.janymq.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Pipleline {
    /**
     * 流水线名称、ID
     */
    String value();

    /**
     * 流水线版本号，参数级修改更新时使用，默认版本号为default
     */
    String version() default "default";

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
