package cn.mzhong.janytask.consumer;

import cn.mzhong.janytask.bean.TestBean;
import cn.mzhong.janytask.producer.TestTask;
import cn.mzhong.janytask.queue.Consumer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Consumer
public class TestTaskImpl implements TestTask {

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
        return true;
    }
}
