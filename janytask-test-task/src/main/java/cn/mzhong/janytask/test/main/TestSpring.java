package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.application.TaskApplication;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.queue.ack.DoneListener;
import cn.mzhong.janytask.queue.ack.ErrorListener;
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
                        .listen(new DoneListener<String>() {
                            public void done(String result) {
                                System.out.println("监听1：" + result);
                            }
                        })
                        .listen(new ErrorListener<String>() {
                            public void error(Throwable throwable) {

                            }
                        });
                System.out.println("得到1：" + ack.get());
                System.out.println("得到2：" + ack.get());
                ack.listen(new DoneListener<String>() {
                    public void done(String result) {
                        System.out.println("已经完成了：" + result);
                    }
                });
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
