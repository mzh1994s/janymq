package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.consumer.TaskConsumerCreator;
import cn.mzhong.janytask.consumer.TaskConsumerInitializer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Spring方式的消费者初始化程序
 *
 * @author mzhong
 * @since 1.0.0
 */
public class TaskSpringCosumerInitializer extends TaskConsumerInitializer {

    protected ConfigurableListableBeanFactory beanFactory;
    protected BeanDefinitionRegistry beanDefinitionRegistry;
    protected int cnt = 0;

    public TaskSpringCosumerInitializer(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanFactory = beanFactory;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        super.consumerCreator = new SpringConsumerCreator();
    }

    class SpringConsumerCreator implements TaskConsumerCreator {

        public Object createConsumer(Class<?> consumerClass) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(consumerClass);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            String beanName = "janytask-consumer#" + (cnt++);
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
            return beanFactory.getBean(beanName);
        }
    }
}
