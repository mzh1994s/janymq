package cn.mzhong.janymq.consumer;

import cn.mzhong.janymq.annotation.Consumer;
import cn.mzhong.janymq.producer.TestMQ;

import javax.annotation.PostConstruct;

@Consumer
public class TestMQImpl implements TestMQ {

    @PostConstruct
    public void init(){
        System.out.println("init");
    }

    @Override
    public void testPipleline(String value) {
        System.out.println(value);
    }

    @Override
    public boolean testLoopline(String value) {
        System.out.println(value);
        return true;
    }
}
