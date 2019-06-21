package cn.mzhong.janymq.consumer;

import cn.mzhong.janymq.annotation.Consumer;
import cn.mzhong.janymq.producer.TestMQ;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Consumer
public class TestMQImpl implements TestMQ {

    @PostConstruct
    public void init(){
        System.out.println("init");
    }

    @Resource
    TestBean testBean;

    @Override
    public void testPipleline(String value) {
        System.out.println("testPipleline：" + value);
        System.out.println(testBean);
    }

    @Override
    public boolean testLoopline(String value) {
        System.out.println("testLoopline：" + value);
        return false;
    }
}
