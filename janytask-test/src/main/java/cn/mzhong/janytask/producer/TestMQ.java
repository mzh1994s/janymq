package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.annotation.Loopline;
import cn.mzhong.janytask.annotation.Pipleline;
import cn.mzhong.janytask.annotation.Producer;

@Producer
public interface TestMQ {

    @Pipleline
    void testPipleline(String value);

    @Loopline
    boolean testLoopline(String value);
}
