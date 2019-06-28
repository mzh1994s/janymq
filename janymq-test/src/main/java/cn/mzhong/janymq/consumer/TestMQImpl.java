package cn.mzhong.janymq.consumer;

import cn.mzhong.janymq.annotation.Consumer;
import cn.mzhong.janymq.producer.TestMQ;

@Consumer
public class TestMQImpl implements TestMQ {

    @Override
    public void testPipleline(String value) {
        System.out.println(value);
    }

    @Override
    public boolean testLoopline(String value) {
        System.out.println(value);
        return false;
    }
}
