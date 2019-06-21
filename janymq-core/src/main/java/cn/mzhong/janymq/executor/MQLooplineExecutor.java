package cn.mzhong.janymq.executor;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.Loopline;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MQLooplineExecutor extends MQLineExecutor {

    final static Logger Log = LoggerFactory.getLogger(MQLooplineExecutor.class);

    protected Loopline loopline;

    public MQLooplineExecutor(MQContext context, Object consumer, Method method, Loopline loopline) {
        super(context,
                context.getLineManagerMap().get(loopline.ID()),
                method,
                consumer,
                ValueUtils.uLong(loopline.getIdleInterval(), context.getLooplineConfig().getIdleInterval()),
                ValueUtils.uLong(loopline.getSleepInterval(), context.getLooplineConfig().getSleepInterval()));
        this.loopline = loopline;
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
