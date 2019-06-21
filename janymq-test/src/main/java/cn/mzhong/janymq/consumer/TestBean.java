package cn.mzhong.janymq.consumer;

import cn.mzhong.janymq.producer.TestMQ;
import org.springframework.beans.factory.annotation.Autowired;

public class TestBean {

    @Autowired
    TestMQ testMQ;

    int piplelineCnt = 0;
    int looplineCnt = 0;

    public void testPipleline() {
        testMQ.testPipleline("Pipleline:value=" + String.valueOf(piplelineCnt++));
    }

    public void testLoopline() {
        testMQ.testLoopline("Loopline:value=" + String.valueOf(looplineCnt++));
    }
}
