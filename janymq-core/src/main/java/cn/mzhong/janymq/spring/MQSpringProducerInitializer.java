package cn.mzhong.janymq.spring;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.producer.MQProducerFactory;
import cn.mzhong.janymq.initializer.MQProducerInitializer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Spring方式的生产者初始化程序
 *
 * @author mzhong
 * @since 1.0.0
 */
public class MQSpringProducerInitializer extends MQProducerInitializer {

    ConfigurableListableBeanFactory beanFactory;
    BeanDefinitionRegistry beanDefinitionRegistry;

    public MQSpringProducerInitializer(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void init(MQContext context) {
        Map<Class<?>, Object> producers = new HashMap<Class<?>, Object>();
        int index = 0;
        // 遍历扫描到的所有生产者类
        Iterator<Class<?>> iterator = context.getProducerClassSet().iterator();
        while (iterator.hasNext()) {
            Class<?> producerClass = iterator.next();
            // 此处使用Spring工厂模式创建生产者
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MQProducerFactory.class);
            // 工厂方法为newInstance
            builder.setFactoryMethod("newInstance");
            // 工厂方法的两个参数
            builder.addConstructorArgValue(context);
            builder.addConstructorArgValue(producerClass);
            // 单例
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            // 当前SpringContext下可能会有消费者和提供者两种实现，使用Autowired只能注入提供者
            // 即 提供者的Primary = true，消费者的 Primary = false.
            // 一般情况下不直接注入消费者，而是通过提供者发送消息间接调用消费者。
            // 当在一个应用中需要同步实现时可通过Resource或者Autowired + Qualifier的方式实现。
            beanDefinition.setPrimary(true);
            // 注册bean到Spring容器
            String beanName = "jSimplemqProducer#" + (index++);
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            // 通过容器获取Bean，并放在JSimpleMQ的Context中
            producers.put(producerClass, beanFactory.getBean(beanName));
        }
        context.setProducerMap(producers);
    }
}
