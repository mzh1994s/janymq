**快速开始**
<br/>
1普通应用方式<br>
TestMQ.java
<pre>
package cn.mzhong.janymq.test;

import Pipleline;
import Producer;

import java.util.List;
import java.util.Map;

@Producer
public interface TestMQ {

    @Pipleline("TestMQ")
    void testVoid();

    @Pipleline(value = "TestMQ", version = "string")
    void testString(String value);

    @Pipleline(value = "TestMQ", version = "list")
    void testList(List<String> value);

    @Pipleline(value = "TestMQ", version = "map")
    void testMap(Map<String, String> value);
}
</pre>
TestMQImpl.java
<br>
<pre>
package cn.mzhong.janymq.test;

import Consumer;

import java.util.List;
import java.util.Map;

@Consumer
public class TestMQImpl implements TestMQ {

    public void testVoid() {
        System.out.println("testVoid");
    }

    public void testString(String value) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("已处理" + value);
    }

    public void testList(List<String> value) {
        System.out.println("testList" + value);
    }

    public void testMap(Map<String, String> value) {
        System.out.println("testMap" + value);
    }
}
</pre>
TestMain.java
<br>
<pre>
package cn.mzhong.janymq.test;

import cn.mzhong.janymq.core.MQApplication;
import cn.mzhong.janymq.producer.TestMQ;
import cn.mzhong.janymq.redis.RedisLineManagerProvider;
import cn.mzhong.janymq.util.ThreadUtils;

public class TestMain {

    public static void main(String[] args) {
        // 启动消费者
        RedisLineManagerProvider manager = new RedisLineManagerProvider();
        manager.setHostName("mzhong.cn");
        manager.setPort(6379);
        manager.setTimeout(1500);
        manager.init();
        MQApplication application = new MQApplication();
        application.setLineManagerProvider(manager);
        application.init();
        // 测试任务
        while (true) {
            TestMQ testMQ = application.getProducer(TestMQ.class);
            testMQ.testPipleline("123");
            testMQ.testLoopline("321");
            ThreadUtils.sleep(1000);
        }
    }
}
</pre>

2、Spring 方式<br>
spring-janymq.xml
<br>
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:janymq="http://www.mzhong.cn/schema/janymq" xmlns:bean="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.mzhong.cn/schema/janymq http://www.mzhong.cn/schema/janymq.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd"&gt;
    &lt;!--redis 实现--&gt;
    &lt;janymq:application basePackage="cn.mzhong"&gt;
        &lt;janymq:redis hostName="mzhong.cn" port="6379"/&gt;
    &lt;/janymq:application&gt;
&lt;/beans&gt;
</pre>
TestSpring.java
<pre>
package cn.mzhong.janymq.test;

import cn.mzhong.janymq.consumer.TestBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-janymq.xml");
        TestBean testBean = context.getBean(TestBean.class);
        int cnt = 1;
        while (cnt >= 0) {
            testBean.testPipleline();
            testBean.testLoopline();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
</pre>