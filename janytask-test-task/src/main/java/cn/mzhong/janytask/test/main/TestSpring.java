package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.test.jdbc.producer.JdbcTestTask;
import cn.mzhong.janytask.test.redis.producer.RedisTaskTask;
import cn.mzhong.janytask.test.zk.producer.ZkTestTask;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationcontext.xml");
        ((ClassPathXmlApplicationContext) context).start();
        RedisTaskTask redisTaskTask = context.getBean(RedisTaskTask.class);
        JdbcTestTask jdbcTestTask = context.getBean(JdbcTestTask.class);
        ZkTestTask zkTestTask = context.getBean(ZkTestTask.class);
        while (true) {
//            redisTaskTask.testLoopline("loopline");
//            redisTaskTask.testPipleline("pipleline");
//            jdbcTestTask.testForJdbc("jdbccc");
//            jdbcTestTask.testForJdbcLoopline("3werew");
//            zkTestTask.testForZkLoopline("zkzk");
//            zkTestTask.testForZkPipleline("zkzk");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
