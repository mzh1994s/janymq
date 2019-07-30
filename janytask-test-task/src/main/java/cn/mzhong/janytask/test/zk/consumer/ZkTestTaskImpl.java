package cn.mzhong.janytask.test.zk.consumer;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.test.zk.producer.ZkTestTask;

@Consumer
public class ZkTestTaskImpl implements ZkTestTask {

    public void testForZkPipleline(String data) {
        System.out.println("testForZkPipleline:" + data);
    }

    public boolean testForZkLoopline(String data) {
        System.out.println("testForZkLoopline:" + data);
        return false;
    }
}
