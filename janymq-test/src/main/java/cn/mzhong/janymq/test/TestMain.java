package cn.mzhong.janymq.test;

import cn.mzhong.janymq.core.MQApplication;
import cn.mzhong.janymq.producer.TestMQ;
import cn.mzhong.janymq.redis.RedisLineManagerProvider;
import cn.mzhong.janymq.util.ThreadUtils;

public class TestMain {

    public static void main(String[] args) {
        MQApplication application = new MQApplication();
        // 启动消费者
        RedisLineManagerProvider manager = RedisLineManagerProvider.create("mzhong.cn", 6379);
        application.setLineManagerProvider(manager);
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
