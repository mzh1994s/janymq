package cn.mzhong.janytask.test.zk.producer;

import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.queue.loopline.Loopline;
import cn.mzhong.janytask.queue.pipleline.Pipeline;

@Producer
public interface ZkTestTask {

    @Pipeline
    void testForZkPipleline(String data);

    @Loopline
    boolean testForZkLoopline(String data);
}
