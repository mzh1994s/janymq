package cn.mzhong.janytask.test;

import cn.mzhong.janytask.core.TaskApplication;
import cn.mzhong.janytask.producer.TestMQ;
import cn.mzhong.janytask.provider.redis.RedisProvider;
import cn.mzhong.janytask.util.ThreadUtils;

public class TestMain {

    public static void main(String[] args) {
        TaskApplication application = new TaskApplication();
        // 启动消费者
        RedisProvider manager = RedisProvider.create("mzhong.cn", 6379);
        application.setQueueProvider(manager);
        application.init();
        // 测试任务
        while (true) {
            TestMQ testMQ = application.getProducer(TestMQ.class);
//            testMQ.testPipleline("123");
//            testMQ.testLoopline("321");
            ThreadUtils.sleep(1000);
        }
    }
}
