package cn.mzhong.janymq.consumer;

import cn.mzhong.janymq.producer.TestMQ;
import org.springframework.beans.factory.annotation.Autowired;

public class TestBean {

    @Autowired
    TestMQ testMQ;

    public void testPipleline(){
        testMQ.testPipleline("哈哈");
    }

    public void testLoopline(){
        testMQ.testLoopline("哈哈");
    }

    @Override
    public String toString() {
        return "TestBean{}";
    }
}
