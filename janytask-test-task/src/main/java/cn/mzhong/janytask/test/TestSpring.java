package cn.mzhong.janytask.test;

import cn.mzhong.janytask.producer.TestMQ;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext.xml");
        TestMQ testBean = context.getBean(TestMQ.class);
        int cnt = 1;
        while (cnt >= 0) {
            testBean.testLoopline("loopline");
            testBean.testPipleline("pipleline");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
