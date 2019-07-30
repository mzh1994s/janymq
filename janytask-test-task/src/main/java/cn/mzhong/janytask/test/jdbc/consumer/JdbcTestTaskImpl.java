package cn.mzhong.janytask.test.jdbc.consumer;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.test.jdbc.producer.JdbcTestTask;

@Consumer
public class JdbcTestTaskImpl implements JdbcTestTask {

    public void testForJdbc(String data) {
        System.out.println("testForJdbc：" + data);
    }

    public boolean testForJdbcLoopline(String data) {
        System.out.println("testForJdbcLoopline：" + data);
        return false;
    }
}
