package cn.mzhong.janytask.schedule;

import java.lang.annotation.*;

/**
 * 定时任务
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
}
