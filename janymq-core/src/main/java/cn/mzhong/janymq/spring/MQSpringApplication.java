package cn.mzhong.janymq.spring;

import cn.mzhong.janymq.core.MQApplication;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

/**
 * Spring JSimpleMQ应用程序
 */
public class MQSpringApplication extends MQApplication implements BeanDefinitionRegistryPostProcessor {

    protected BeanDefinitionRegistry beanDefinitionRegistry;
    protected ConfigurableListableBeanFactory beanFactory;

    protected static void addBeanPostProcessor(ConfigurableListableBeanFactory factory, BeanFactoryAware beanPostProcessor) {
        beanPostProcessor.setBeanFactory(factory);
        factory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        // 支持Autowired、Value注解
        addBeanPostProcessor(beanFactory, new AutowiredAnnotationBeanPostProcessor());
        // 支持Resource、PostConstruct等注解
        addBeanPostProcessor(beanFactory, new CommonAnnotationBeanPostProcessor());
        // 使用Spring初始化程序
        this.producerInitializer = new MQSpringProducerInitializer(beanFactory, beanDefinitionRegistry);
        this.consumerInitializer = new MQSpringCosumerInitializer(beanFactory, beanDefinitionRegistry);
        this.init();
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }
}
