package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.producer.TestMQ;

@Consumer
public class TestMQImpl implements TestMQ {

    public void testPipleline(String value) {
        System.out.println(value);
    }

    public boolean testLoopline(String value) {
        System.out.println(value);
        return false;
    }
}
