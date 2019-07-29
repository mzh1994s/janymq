package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.queue.provider.QueueProvider;

import java.util.Arrays;

/**
 * 多个提供者之间不得扫描到重复的Class
 */
public class OverlapClassException extends RuntimeException {

    public OverlapClassException(Class<?> _class, QueueProvider provider1, QueueProvider provider2) {
        super("检测到两个提供商扫描到相同的类：" + _class.getName()
                + "；提供商1：" + provider1.getClass().getName() + "，提供的包：" + Arrays.toString(provider1.getPackages())
                + "；提供商2：" + provider2.getClass().getName() + "提供的包：" + Arrays.toString(provider2.getPackages()));
    }
}
