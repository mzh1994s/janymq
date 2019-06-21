package cn.mzhong.janymq.producer;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.annotation.Producer;

@Producer
public interface TestMQ {

    @Pipleline
    void testPipleline(String value);

    @Loopline
    boolean testLoopline(String value);
}
