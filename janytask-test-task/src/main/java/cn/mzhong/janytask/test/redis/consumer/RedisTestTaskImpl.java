package cn.mzhong.janytask.test.redis.consumer;

import cn.mzhong.janytask.test.bean.TestBean;
import cn.mzhong.janytask.test.redis.producer.RedisTaskTask;
import cn.mzhong.janytask.queue.Consumer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Consumer
public class RedisTestTaskImpl implements RedisTaskTask {

    @Autowired
    TestBean testBean;

    @PostConstruct
    public void init(){
        System.out.println(testBean);
    }

    public void testPipleline(String value) {
        System.out.println(value);
    }

    public boolean testLoopline(String value) {
        System.out.println(value);
        return false;
    }
}
