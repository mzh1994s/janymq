package cn.mzhong.janytask.test;

import cn.mzhong.janytask.core.TaskApplication;
import cn.mzhong.janytask.producer.TestTask;
import cn.mzhong.janytask.provider.redis.RedisProvider;

public class TestMain {

    public static void main(String[] args) {
        TaskApplication application = new TaskApplication();
        // 启动消费者
        RedisProvider manager = RedisProvider.create("mzhong.cn", 6379);
        application.setQueueProvider(manager);
        application.start();
        // 测试任务
        while (true) {
            TestTask testMQ = application.getProducer(TestTask.class);
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
