package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.application.TaskApplication;
import cn.mzhong.janytask.test.redis.producer.RedisTaskTask;
import cn.mzhong.janytask.queue.provider.redis.RedisProvider;

public class TestMain {

    public static void main(String[] args) {
        TaskApplication application = new TaskApplication();
        // 启动消费者
        RedisProvider manager = RedisProvider.create("mzhong.cn", 6379);
        application.addProvider(manager);
        application.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        application.close();
        // 测试任务
        while (true) {
            RedisTaskTask testMQ = application.getProducer(RedisTaskTask.class);
//            testMQ.testPipleline("123");
//            testMQ.testLoopline("321");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
