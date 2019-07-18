package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.core.TaskApplication;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

/**
 * Spring janytask应用程序
 */
public class TaskSpringApplication extends TaskApplication implements BeanDefinitionRegistryPostProcessor {

    protected BeanDefinitionRegistry beanDefinitionRegistry;
    protected ConfigurableListableBeanFactory beanFactory;

    protected void addBeanPostProcessor(ConfigurableListableBeanFactory factory, BeanFactoryAware beanPostProcessor) {
        beanPostProcessor.setBeanFactory(factory);
        factory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        // 支持Autowired、Value注解
        this.addBeanPostProcessor(beanFactory, new AutowiredAnnotationBeanPostProcessor());
        // 支持Resource、PostConstruct等注解
        this.addBeanPostProcessor(beanFactory, new CommonAnnotationBeanPostProcessor());
        // 使用Spring生成生产者和消费者
        queueManager.setProducerCreator(
                new SpringProducerCreator(beanFactory, beanDefinitionRegistry,
                        queueManager.getMethodMessageDaoMap()));
        queueManager.setConsumerCreator(new SpringConsumerCreator(beanFactory, beanDefinitionRegistry));
        this.init();
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }
}
