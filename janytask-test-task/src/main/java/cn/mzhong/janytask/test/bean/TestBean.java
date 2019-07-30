package cn.mzhong.janytask.test.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TestBean {

    @Autowired
    ApplicationContext context;

    @PostConstruct
    private void init(){
    }
}
