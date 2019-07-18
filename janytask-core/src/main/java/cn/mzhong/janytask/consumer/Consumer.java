package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.core.JanyTask;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JanyTask
public @interface Consumer {

}
