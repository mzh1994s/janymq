package cn.mzhong.janytask.producer;

import cn.mzhong.janytask.queue.loopline.Loopline;
import cn.mzhong.janytask.queue.pipleline.Pipleline;

@Producer
public interface TestMQ {

    @Pipleline(version = "1.0.0", cron = "0/5 * * * * ?")
    void testPipleline(String value);

    @Loopline
    boolean testLoopline(String value);
}
