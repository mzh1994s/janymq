package cn.mzhong.janymq.core;

import cn.mzhong.janymq.annotation.Consumer;
import cn.mzhong.janymq.annotation.Producer;
import cn.mzhong.janymq.config.ApplicationConfig;
import cn.mzhong.janymq.config.LooplineConfig;
import cn.mzhong.janymq.config.PiplelineConfig;
import cn.mzhong.janymq.exception.MQInitExcepition;
import cn.mzhong.janymq.exception.MQNotFoundException;
import cn.mzhong.janymq.initializer.MQConsumerInitializer;
import cn.mzhong.janymq.initializer.MQProducerInitializer;
import cn.mzhong.janymq.initializer.MQLineManagerInitializer;
import cn.mzhong.janymq.line.DataSerializer;
import cn.mzhong.janymq.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JSimple的核心思想就是让异步调用变得容易。就像调用一个同步方法一样去实现消息队列的异步操作，
 * 您可以直接用new关键字创建一个或多个相互不干扰的JSimpleMQ应用程序,使用时直接使用创建的Application
 * 获取生产者，再进行生产者相关的调用，调用时与普通方法调用一样。在JSimpleMQ中，消费者及生产者，
 * 你只需编写消费者代码即可由JSimple应用程序获取到生产者的代理，而且编写一个消费者也特别容易。
 *
 * @author mzhong
 * @version 1.0.0
 */
public class MQApplication extends MQContext {

    final static Logger Log = LoggerFactory.getLogger(MQApplication.class);

    protected void wellcome() {
        Log.debug(this.applicationConfig.toString());
        Log.debug(this.piplelineConfig.toString());
        Log.debug(this.looplineConfig.toString());
        Log.debug("JSimpleMQ application started!");
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
        if (lineManagerProvider == null) {
            throw new MQInitExcepition("LineManagerBuilder不存在，请先指定LineManagerBuilder");
        }
        if (dataSerializer == null) {
            dataSerializer = new DataSerializer();
        }
        if (LineManagerInitializer == null) {
            LineManagerInitializer = new MQLineManagerInitializer();
        }
        if (consumerInitializer == null) {
            consumerInitializer = new MQConsumerInitializer();
        }
        if (producerInitializer == null) {
            producerInitializer = new MQProducerInitializer();
        }
        setDataSerializer(dataSerializer);
        // 扫描所有的消费者
        setConsumerClassSet(ClassUtils.scanByAnnotation(applicationConfig.getBasePackage(), Consumer.class));
        // 扫描所有的生产者
        setProducerClassSet(ClassUtils.scanByAnnotation(applicationConfig.getBasePackage(), Producer.class));
        LineManagerInitializer.init(this);
        producerInitializer.init(this);
        consumerInitializer.init(this);
        // 正常终结
        Runtime.getRuntime().addShutdownHook(new MQShutdownHook(this));
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
            throw new MQNotFoundException("未在当前上下文中找到生产者：" + producerClass.getName());
        }
        return (T) producer;
    }

}
