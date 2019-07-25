package cn.mzhong.janytask.core;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.QueueConfig;
import cn.mzhong.janytask.queue.JdkDataSerializer;
import cn.mzhong.janytask.queue.NoSuchProducerException;
import cn.mzhong.janytask.queue.provider.NoAnyProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Janytask
 *
 * @author mzhong
 * @version 2.0.0
 * @date 2019年7月10日
 */
public class TaskApplication extends TaskContext {
    final static Logger Log = LoggerFactory.getLogger(TaskApplication.class);


    protected void wellcome() {
        Log.debug(this.applicationConfig.toString());
        Log.debug(this.queueConfig.toString());
        Log.debug("janytask application started!");
    }

    public TaskApplication() {
        this.queueManager.setContext(this);
        this.scheduleManager.setContext(this);
        this.taskWorker.setContext(this);
    }

    public void start() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        if (queueConfig == null) {
            queueConfig = new QueueConfig();
        }
        if (queueProvider == null) {
            throw new NoAnyProviderException("未找到提供商，请先指定提供商！");
        }
        if (dataSerializer == null) {
            dataSerializer = new JdkDataSerializer();
        }
        this.setDataSerializer(dataSerializer);
        this.queueProvider.setContext(this);
        // 调用组件初始化程序
        this.queueProvider.init();
        this.queueManager.init();
        this.scheduleManager.init();
        this.taskWorker.init();

        // 正常终结
        Runtime.getRuntime().addShutdownHook(new TaskShutdownHook(this));

        // 启动worker
        this.taskWorker.start();
        this.wellcome();
    }

    @SuppressWarnings({"SingleStatementInBlock", "unchecked"})
    public <T> T getProducer(Class<T> producerClass) {
        Map<Class<?>, Object> producerMap = this.queueManager.getProducerMap();
        Object producer = producerMap.get(producerClass);
        if (producer == null) {
            for (Map.Entry<Class<?>, Object> entry : producerMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(producerClass)) {
                    return (T) entry.getValue();
                }
            }
            throw new NoSuchProducerException("未在当前上下文中找到生产者：" + producerClass.getName());
        }
        return (T) producer;
    }

    public void close() {
        synchronized (this) {
            if (!isShutdown()) {
                this.shutdownHook.run();
            }
        }
    }
}
