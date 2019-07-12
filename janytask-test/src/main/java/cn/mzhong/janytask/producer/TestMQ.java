package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.loopline.Loopline;
import cn.mzhong.janytask.pipleline.Pipleline;

@Producer
public interface TestMQ {

    @Pipleline
    void testPipleline(String value);

    @Loopline
    boolean testLoopline(String value);
}
