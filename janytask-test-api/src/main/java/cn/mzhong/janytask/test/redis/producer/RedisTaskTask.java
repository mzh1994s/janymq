package cn.mzhong.janytask.test.redis.producer;

import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.queue.pipleline.Pipeline;

@Producer
public interface RedisTaskTask {

    @Pipeline(version = "1.0.0", cron = "0/5 * * * * ?")
    void testPipleline(String value);
}
