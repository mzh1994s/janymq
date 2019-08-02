# Janytask分布式任务框架<br/>
_宗旨：用尽可能少的维护成本搭建任务平台，用尽可能简单的调用方式处理异步任务。_<br/>
目前已支持Redis、Zookeeper、数据库（mysql、oracle）作为中间件支撑任务运行。后面会加入更多的中间件作为提供商（Provider）<br/>


# 快速开始<br/>
第一步：编写生产者和消费者<br/>
第二步：指定提供商，启动应用程序<br/>

## 1、生产者和消费者
### 1.1、生产者
我们现在都是面向接口编程，编写一个接口类，加上@Producer注解，标注这个接口是一个生产者接口，在具体方法上加上@Pipeline注解，那么
这个方法就被认定为一条流水线，生产者通过流水线顶端发送任务，消费者则从末端接收任务。生产者使用代理类发送消息。<br/>
TestMQ.java
<pre>
@Producer
public interface TestTask {

    @Pipeline("TestMQ")
    void testVoid();

    @Pipeline(value = "TestMQ", version = "1.0.0")
    void testString(String value);

    @Pipeline(value = "TestMQ", version = "2.0.0")
    void testList(List<String> value);

    @Pipeline
    void testMap(Map<String, String> value);
}
</pre>
### 1.2、消费者
消费者是生产者的实现，将接收到的参数进行处理，与Spring中的Service、ServiceImpl不同的地方是Producer、Consumer之间仅仅是规范如
何传递消息，当你在Spring应用程序中使用@Autowired注入一个生产者（比如TestTask）时，其实现方并不是TestTaskImpl，而是Producer的
一个内部代理类。消费者每个线程由框架统一调度。<br/>
TestMQImpl.java
<pre>
@Consumer
public class TestTaskImpl implements TestTask {

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
## 2、指定提供商，启动应用程序
### 2.1、普通应用方式
<pre>
public class TestMain {

    public static void main(String[] args) {
        // 启动消费者
        TaskApplication application = new TaskApplication();
        // Janytask依靠第三方提供商运行，必须指定提供商
        RedisProvider manager = RedisProvider.create("mzhong.cn", 6379);
        application.setQueueProvider(manager);
        application.init();
        TestTask testTask = application.getProducer(TestTask.class);
        // 测试任务
        while (true) {
            testTask.testVoid();
            testTask.testString("321");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
</pre>

### 2.2、Spring 方式<br>
spring-janytask.xml
<br>
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:janymq="http://www.mzhong.cn/schema/janytask" xmlns:bean="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.mzhong.cn/schema/janytask http://www.mzhong.cn/schema/janymq.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd"&gt;
    
    &lt;janytask:application name="test-application" /&gt
    &lt;!--可以有多个提供商，只要他们的package不含交集--&gt;
    &lt;!--redis 实现--&gt;
    &lt;janytask:provider-redis package="cn.mzhong.janytask.test.redis"
                                 host="${janytask.provider.redis.host}"
                                 port="6379" /&gt
                                 
    &lt;!--jdbc 实现--&gt;
    &lt;janytask:provider-jdbc package="cn.mzhong.janytask.test.jdbc"
                                 dataSource-ref="mysqlDataSource"//&gt
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
            testMQ.testString("任意内容的可序列号的参数");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
</pre>