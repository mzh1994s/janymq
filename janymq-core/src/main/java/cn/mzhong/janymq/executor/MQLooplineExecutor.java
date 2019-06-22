package cn.mzhong.janymq.executor;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MQLooplineExecutor extends MQLineExecutor {

    final static Logger Log = LoggerFactory.getLogger(MQLooplineExecutor.class);

    protected LooplineInfo looplineInfo;

    public MQLooplineExecutor(MQContext context, Object consumer, Method method, LooplineInfo looplineInfo) {
        super(context,
                context.getMethodLineManagerMap().get(looplineInfo.getMethod()),
                method,
                consumer,
                ValueUtils.uLong(looplineInfo.getIdleInterval(), context.getLooplineConfig().getIdleInterval()),
                ValueUtils.uLong(looplineInfo.getSleepInterval(), context.getLooplineConfig().getSleepInterval()));
        this.looplineInfo = looplineInfo;
    }


    @Override
    void invoke(Message message) {
        try {
            boolean res = (boolean) method.invoke(consumer, (Object[]) message.getData());
            if (res) {
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
