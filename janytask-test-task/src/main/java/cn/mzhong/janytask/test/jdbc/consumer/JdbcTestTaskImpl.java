package cn.mzhong.janytask.test.jdbc.consumer;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.test.jdbc.producer.JdbcTestTask;

@Consumer
public class JdbcTestTaskImpl implements JdbcTestTask {

    public Ack<String> testForJdbc(String data) {
        System.out.println("testForJdbcPipleline：" + data);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Ack<String>("收到：" + data);
    }

    public boolean testForJdbcLoopline(String data) {
        System.out.println("testForJdbcLoopline：" + data);
        return true;
    }
}
