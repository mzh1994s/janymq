package cn.mzhong.janytask.test.zk.producer;

import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.queue.loopline.Loopline;
import cn.mzhong.janytask.queue.pipleline.Pipleline;

@Producer
public interface ZkTestTask {

    @Pipleline
    void testForZkPipleline(String data);

    @Loopline
    boolean testForZkLoopline(String data);
}
