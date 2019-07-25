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
 * Janytask的核心思想就是让异步调用变得容易。就像调用一个同步方法一样去实现消息队列的异步操作，
 * 使用{@link TaskApplication#getInctance()}获取Janytask应用程序,使用创建的Application获取生产者<br/>
 * 日记：<br/>
 * 2019年7月25日15:54:39 将应用程序修改为单例模式，一个Jvm中只存在一个Janytask应用程序，但可以支持多个提供商.<br/>
 *
 * @author mzhong
 * @version 2.0.0
 * @date 2019年7月10日
 */
public class TaskApplication extends TaskContext {

    private static TaskApplication instance = null;
    private boolean started;

    final static Logger Log = LoggerFactory.getLogger(TaskApplication.class);

    protected void wellcome() {
        Log.debug(this.applicationConfig.toString());
        Log.debug(this.queueConfig.toString());
        Log.debug("janytask application started!");
    }

    public static TaskApplication getInctance() {
        if (instance == null) {
            synchronized (TaskApplication.class) {
                if (instance == null) {
                    instance = new TaskApplication();
                }
            }
        }
        return instance;
    }

    protected TaskApplication() {
        this.queueManager.setContext(this);
        this.scheduleManager.setContext(this);
        this.taskWorker.setContext(this);
    }

    public void start() {
        synchronized (instance) {
            if (started) {
                throw new RuntimeException("Janytask应用程序已经启动！");
            }
            started = true;
        }
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

}
