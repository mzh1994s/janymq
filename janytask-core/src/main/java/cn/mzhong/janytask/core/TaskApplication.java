package cn.mzhong.janytask.core;

import cn.mzhong.janytask.annotation.Consumer;
import cn.mzhong.janytask.annotation.Producer;
import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.LooplineConfig;
import cn.mzhong.janytask.config.PiplelineConfig;
import cn.mzhong.janytask.exception.TaskInitExcepition;
import cn.mzhong.janytask.exception.TaskNotFoundException;
import cn.mzhong.janytask.consumer.TaskConsumerInitializer;
import cn.mzhong.janytask.producer.TaskProducerInitializer;
import cn.mzhong.janytask.queue.TaskQueueManagerInitializer;
import cn.mzhong.janytask.queue.JdkDataSerializer;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JSimple的核心思想就是让异步调用变得容易。就像调用一个同步方法一样去实现消息队列的异步操作，
 * 您可以直接用new关键字创建一个或多个相互不干扰的janytask应用程序,使用时直接使用创建的Application
 * 获取生产者，再进行生产者相关的调用，调用时与普通方法调用一样。在janytask中，消费者及生产者，
 * 你只需编写消费者代码即可由janytask应用程序获取到生产者的代理，而且编写一个消费者也特别容易。
 *
 * @author mzhong
 * @version 1.0.0
 */
public class TaskApplication extends TaskContext {

    final static Logger Log = LoggerFactory.getLogger(TaskApplication.class);

    protected void wellcome() {
        Log.debug(this.applicationConfig.toString());
        Log.debug(this.piplelineConfig.toString());
        Log.debug(this.looplineConfig.toString());
        Log.debug("janytask application started!");
    }

    /**
     * JSimpleMQ的初始化方法，应用程序的开始。
     */
    public void init() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        if (looplineConfig == null) {
            looplineConfig = new LooplineConfig();
        }
        if (piplelineConfig == null) {
            piplelineConfig = new PiplelineConfig();
        }
        if (queueProvider == null) {
            throw new TaskInitExcepition("LineManagerBuilder不存在，请先指定LineManagerBuilder");
        }
        if (dataSerializer == null) {
            dataSerializer = new JdkDataSerializer();
        }
        if (providerInitializer == null) {
            providerInitializer = new TaskQueueManagerInitializer();
        }
        if (consumerInitializer == null) {
            consumerInitializer = new TaskConsumerInitializer();
        }
        if (producerInitializer == null) {
            producerInitializer = new TaskProducerInitializer();
        }
        setDataSerializer(dataSerializer);
        // 扫描所有的消费者
        setConsumerClassSet(ClassUtils.scanByAnnotation(applicationConfig.getBasePackage(), Consumer.class));
        // 扫描所有的生产者
        setProducerClassSet(ClassUtils.scanByAnnotation(applicationConfig.getBasePackage(), Producer.class));
        providerInitializer.init(this);
        producerInitializer.init(this);
        consumerInitializer.init(this);
        // 正常终结
        Runtime.getRuntime().addShutdownHook(new TaskShutdownHook(this));
        this.wellcome();
    }

    public <T> T getProducer(Class<T> producerClass) {
        Object producer = producerMap.get(producerClass);
        if (producer == null) {
            for (Map.Entry<Class<?>, Object> entry : producerMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(producerClass)) {
                    return (T) entry.getValue();
                }
            }
            throw new TaskNotFoundException("未在当前上下文中找到生产者：" + producerClass.getName());
        }
        return (T) producer;
    }

}