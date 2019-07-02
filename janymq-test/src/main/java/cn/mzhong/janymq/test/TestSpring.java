package cn.mzhong.janymq.test;

import cn.mzhong.janymq.consumer.TestBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-janymq.xml");
        TestBean testBean = context.getBean(TestBean.class);
        int cnt = 1;
        while (cnt >= 0) {
//            testBean.testPipleline();
            testBean.testLoopline();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
