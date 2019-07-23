***Janytask***<br/>
_宗旨：用尽可能少的维护成本搭建任务平台，用尽可能简单的调用方式处理异步任务。_<br/>
目前已支持Redis、Zookeeper、数据库（mysql、oracle）作为中间件支撑任务运行。后期会加入更多的中间件作为提供商（Provider）<br/>

***快速开始***<br/>
1普通应用方式<br>
TestMQ.java
<pre>
@Producer
public interface TestMQ {

    @Pipleline("TestMQ")
    void testVoid();

    @Pipleline(value = "TestMQ", version = "1.0.0")
    void testString(String value);

    @Pipleline(value = "TestMQ", version = "2.0.0")
    void testList(List<String> value);

    @Pipleline
    void testMap(Map<String, String> value);
}
</pre>
TestMQImpl.java
<br>
<pre>
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
public class TestMain {

    public static void main(String[] args) {
        // 启动消费者
        TaskApplication application = new TaskApplication();
        // Janytask依靠第三方提供商运行，必须指定提供商
        RedisProvider manager = RedisProvider.create("mzhong.cn", 6379);
        application.setQueueProvider(manager);
        application.init();
        TestMQ testMQ = application.getProducer(TestMQ.class);
        // 测试任务
        while (true) {
            testMQ.testVoid();
            testMQ.testString("321");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
       xmlns:janymq="http://www.mzhong.cn/schema/janytask" xmlns:bean="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.mzhong.cn/schema/janytask http://www.mzhong.cn/schema/janymq.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd"&gt;
    &lt;!--redis 实现--&gt;
    &lt;janytask:application basePackage="cn.mzhong"&gt;
        &lt;janytask:redis hostName="mzhong.cn" port="6379"/&gt;
    &lt;/janytask:application&gt;
&lt;/beans&gt;
</pre>
TestSpring.java
<pre>
public class TestSpring {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-janytask.xml");
        TestMQ testBean = context.getBean(TestMQ.class);
        while (cnt >= 0) {
            testMQ.testVoid();
            testMQ.testString("321");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
</pre>