package cn.mzhong.janymq.producer;

import cn.mzhong.janymq.annotation.Loopline;
import cn.mzhong.janymq.annotation.Pipleline;
import cn.mzhong.janymq.annotation.Producer;

@Producer
public interface TestMQ {

    @Pipleline(value = "testPipleline")
    void testPipleline(String value);

    @Loopline(value = "testLoopline")
    boolean testLoopline(String value);
}
