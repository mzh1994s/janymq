package cn.mzhong.janymq.spring;

import cn.mzhong.janymq.core.MQConsumerInitializer;
import cn.mzhong.janymq.core.MQContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Spring方式的消费者初始化程序
 *
 * @author mzhong
 * @since 1.0.0
 */
public class MQSpringCosumerInitializer extends MQConsumerInitializer {

    ConfigurableListableBeanFactory beanFactory;
    BeanDefinitionRegistry beanDefinitionRegistry;

    public MQSpringCosumerInitializer(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void init(MQContext context) {
        Map<Class<?>, Object> consumerMap = new HashMap<>();
        int index = 0;
        Iterator<Class<?>> iterator = context.getConsumerClassSet().iterator();
        while (iterator.hasNext()) {
            Class<?> consumerClass = iterator.next();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(consumerClass);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            String beanName = "jSimplemqConsumer#" + (index++);
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            Object consumer = beanFactory.getBean(beanName);
            consumerMap.put(consumerClass, consumer);
        }
        // 保存消费者Map在Context（注意不是Spring的Context）
        context.setConsumerMap(consumerMap);
        // 调用服务的执行者，开启线程开始工作
        super.initExecutor(context, consumerMap.values());
    }
}
