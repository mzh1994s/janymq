package cn.mzhong.janytask.test.jdbc.producer;

import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.queue.pipleline.Pipeline;

@Producer
public interface JdbcTestTask {

    @Pipeline
    Ack<String> testForJdbc(String data);
}
