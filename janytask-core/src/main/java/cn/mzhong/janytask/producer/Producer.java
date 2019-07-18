package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.core.JanyTask;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@JanyTask
public @interface Producer {

}
