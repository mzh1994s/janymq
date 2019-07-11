package cn.mzhong.janytask.core;

import cn.mzhong.janytask.consumer.ConsumerInfo;
import cn.mzhong.janytask.queue.Message;

public interface MessageProcessor<A> {

    void processMessage(Message message, ConsumerInfo<A> consumerInfo);
}
