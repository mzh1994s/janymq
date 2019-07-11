package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.PiplelineInfo;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TaskPiplelineExecutor extends TaskExecutor {
    final static Logger Log = LoggerFactory.getLogger(TaskPiplelineExecutor.class);

    public TaskPiplelineExecutor(TaskContext context, Object consumer, Method method, PiplelineInfo piplelineInfo) {
        super(context,
                context.getMethodQueueManagerMap().get(piplelineInfo.getProducerMethod()),
                method,
                consumer,
                ValueUtils.uLong(piplelineInfo.getIdleInterval(), context.getPiplelineConfig().getIdleInterval()),
                ValueUtils.uLong(piplelineInfo.getSleepInterval(), context.getPiplelineConfig().getSleepInterval()));
    }

    @Override
    public void invoke(Message message) {
        try {
            method.invoke(consumer, message.getContent());
            queueManager.done(message);
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            queueManager.error(message);
        }
    }
}
