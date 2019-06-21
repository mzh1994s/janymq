package cn.mzhong.janymq.executor;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.Pipleline;
import cn.mzhong.janymq.line.Message;
import cn.mzhong.janymq.util.ValueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MQPiplelineExecutor extends MQLineExecutor {
    final static Logger Log = LoggerFactory.getLogger(MQPiplelineExecutor.class);

    public MQPiplelineExecutor(MQContext context, Object consumer, Method method, Pipleline pipleline) {
        super(context,
                context.getLineManagerMap().get(pipleline.ID()),
                method,
                consumer,
                ValueUtils.uLong(pipleline.getIdleInterval(), context.getPiplelineConfig().getIdleInterval()),
                ValueUtils.uLong(pipleline.getSleepInterval(), context.getPiplelineConfig().getSleepInterval()));
    }

    @Override
    public void invoke(Message message) {
        try {
            Object[] args = (Object[]) message.getData();
            method.invoke(consumer, args);
            lineManager.done(message);
        } catch (Exception e) {
            Log.error(e.getLocalizedMessage(), e);
            message.setThrowable(e);
            lineManager.error(message);
        }
    }
}
