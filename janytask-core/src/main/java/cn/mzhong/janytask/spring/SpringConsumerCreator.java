package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.consumer.ConsumerCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class SpringConsumerCreator implements ConsumerCreator {
    protected ConfigurableListableBeanFactory beanFactory;
    protected BeanDefinitionRegistry beanDefinitionRegistry;
    protected int cnt = 0;

    public SpringConsumerCreator(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    public Object createConsumer(Class<?> consumerClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(consumerClass);
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = "janytask-consumer#" + (cnt++);
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        return beanFactory.getBean(beanName);
    }
}
