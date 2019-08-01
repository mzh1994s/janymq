package cn.mzhong.janytask.test.jdbc.consumer;

import cn.mzhong.janytask.queue.Consumer;
import cn.mzhong.janytask.queue.ack.Ack;
import cn.mzhong.janytask.test.jdbc.producer.JdbcTestTask;

@Consumer
public class JdbcTestTaskImpl implements JdbcTestTask {

    private boolean flag;

    public Ack<String> testForJdbc(String data) {
        System.out.println("testForJdbcPipleline：" + data);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(flag = !flag){
            return new Ack<String>("收到：" + data);
        } else {
            return Ack.back();
        }
    }

    public boolean testForJdbcLoopline(String data) {
        System.out.println("testForJdbcLoopline：" + data);
        return true;
    }
}
