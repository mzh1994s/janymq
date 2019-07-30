package cn.mzhong.janytask.test.jdbc.producer;

import cn.mzhong.janytask.queue.Producer;
import cn.mzhong.janytask.queue.loopline.Loopline;
import cn.mzhong.janytask.queue.pipleline.Pipleline;

@Producer
public interface JdbcTestTask {

    @Pipleline
    void testForJdbc(String data);

    @Loopline
    boolean testForJdbcLoopline(String data);
}
