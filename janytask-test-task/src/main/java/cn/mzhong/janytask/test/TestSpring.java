package cn.mzhong.janytask.test;

import cn.mzhong.janytask.producer.TestTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext.xml");
        ((ClassPathXmlApplicationContext) context).start();
        TestTask testBean = context.getBean(TestTask.class);
        int cnt = 1;
        while (cnt >= 0) {
//            testBean.testLoopline("loopline");
//            testBean.testPipleline("pipleline");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
