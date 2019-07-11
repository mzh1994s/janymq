package cn.mzhong.janytask.core;

import cn.mzhong.janytask.consumer.ConsumerInfo;
import cn.mzhong.janytask.queue.Message;

import java.lang.annotation.Annotation;

public interface MessageProcessor<A extends Annotation> {

    void processMessage(Message message, ConsumerInfo<A> consumerInfo);
}
