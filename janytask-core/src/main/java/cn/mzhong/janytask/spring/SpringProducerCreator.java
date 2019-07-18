package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.producer.ProducerCreator;
import cn.mzhong.janytask.producer.ProducerFactory;
import cn.mzhong.janytask.queue.MessageDao;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SpringProducerCreator implements ProducerCreator {

    protected ConfigurableListableBeanFactory beanFactory;
    protected BeanDefinitionRegistry beanDefinitionRegistry;
    protected Map<Method, MessageDao> methodMessageDaoMap;
    protected int cnt = 0;

    public SpringProducerCreator(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry, Map<Method, MessageDao> methodMessageDaoMap) {
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        this.methodMessageDaoMap = methodMessageDaoMap;
    }

    public Object createProducer(Class<?> _class) {
        // 此处使用Spring工厂模式创建生产者
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProducerFactory.class);
        // 工厂方法为newInstance
        builder.setFactoryMethod("newInstance");
        // 工厂方法的两个参数
        builder.addConstructorArgValue(methodMessageDaoMap);
        builder.addConstructorArgValue(_class);
        // 单例
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        // 当前SpringContext下可能会有消费者和提供者两种实现，使用Autowired只能注入提供者
        // 即 提供者的Primary = true，消费者的 Primary = false.
        // 一般情况下不直接注入消费者，而是通过提供者发送消息间接调用消费者。
        // 当在一个应用中需要同步实现时可通过Resource或者Autowired + Qualifier的方式实现。
        beanDefinition.setPrimary(true);
        // 注册bean到Spring容器
        String beanName = "janytask-producer#" + (cnt++);
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        return beanFactory.getBean(beanName);
    }
}
