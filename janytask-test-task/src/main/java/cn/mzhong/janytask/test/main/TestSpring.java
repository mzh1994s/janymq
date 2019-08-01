package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.application.TaskApplication;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.queue.ack.AckListener;
import cn.mzhong.janytask.test.jdbc.producer.JdbcTestTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext.xml");
        ((ClassPathXmlApplicationContext) context).start();
        TaskApplication application = context.getBean(TaskApplication.class);
//        RedisTaskTask redisTaskTask = context.getBean(RedisTaskTask.class);
        JdbcTestTask jdbcTestTask = context.getBean(JdbcTestTask.class);
//        ZkTestTask zkTestTask = context.getBean(ZkTestTask.class);
        while (true) {
            try {
//            redisTaskTask.testLoopline("loopline");
//            redisTaskTask.testPipleline("pipleline");
                Ack<String> ack = jdbcTestTask.testForJdbc("jdbccc")
                        .addListener(new AckListener<String>() {
                            public void done(String result) {
                                System.out.println("监听1：" + result);
                            }

                            public void error(Throwable throwable) {

                            }
                        })
                        .addListener(new AckListener<String>() {
                            public void done(String result) {
                                System.out.println("监听2：" + result);
                            }

                            public void error(Throwable throwable) {

                            }
                        }).push();
                System.out.println("等待前");
                System.out.println("得到：" + ack.get());
                System.out.println("等待后");
//                jdbcTestTask.testForJdbcLoopline("3werew");
//            zkTestTask.testForZkLoopline("zkzk");
//            zkTestTask.testForZkPipleline("zkzk");
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
