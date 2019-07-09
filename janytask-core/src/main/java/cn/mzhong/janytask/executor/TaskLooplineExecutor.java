package cn.mzhong.janytask.executor;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.LooplineInfo;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TaskLooplineExecutor extends TaskExecutor {

    final static Logger Log = LoggerFactory.getLogger(TaskLooplineExecutor.class);

    protected LooplineInfo looplineInfo;

    public TaskLooplineExecutor(TaskContext context, Object consumer, Method method, LooplineInfo looplineInfo) {
        super(context,
                context.getMethodQueueManagerMap().get(looplineInfo.getMethod()),
                method,
                consumer,
                ValueUtils.uLong(looplineInfo.getIdleInterval(), context.getLooplineConfig().getIdleInterval()),
                ValueUtils.uLong(looplineInfo.getSleepInterval(), context.getLooplineConfig().getSleepInterval()));
        this.looplineInfo = looplineInfo;
    }


    @Override
    void invoke(Message message) {
        try {
            Boolean result = (Boolean) method.invoke(consumer, message.getContent());
            if (result != null && result) {
                lineManager.done(message);
            } else {
                lineManager.back(message);
            }
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            lineManager.error(message);
        }
    }
}
