package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.QueueConfig;
import cn.mzhong.janytask.queue.ConsumerCreator;
import cn.mzhong.janytask.core.TaskApplication;
import cn.mzhong.janytask.queue.QueueManager;
import cn.mzhong.janytask.queue.provider.NoAnyProviderException;
import cn.mzhong.janytask.queue.InstanceCreator;
import cn.mzhong.janytask.queue.ProducerFactory;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.tool.PInvoker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Iterator;
import java.util.Map;

/**
 * Spring janytask应用程序
 */
public class TaskSpringApplication extends TaskApplication implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextRefreshedEvent> {


    protected BeanDefinitionRegistry registry;
    protected ConfigurableListableBeanFactory beanFactory;

    // 生产者生成Bean的方式
    private PInvoker<Class<?>> producerPInvoker = new PInvoker<Class<?>>() {
        int cnt = 0;

        public void invoke(Class<?> producerClass) throws Exception {
            // 此处使用Spring工厂模式创建生产者
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProducerFactory.class);
            // 工厂方法为newInstance
            builder.setFactoryMethod("newInstance");
            // 工厂方法的两个参数
            builder.addConstructorArgValue(TaskSpringApplication.this);
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
            String beanName = "janytask-tproducer#" + (cnt++);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    };
    // 消费者生成Bean的方式
    private PInvoker<Class<?>> consumerPInvoker = new PInvoker<Class<?>>() {
        int cnt = 0;

        public void invoke(Class<?> consumerClass) throws Exception {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(consumerClass);
            builder.setScope(BeanDefinition.SCOPE_SINGLETON);
            BeanDefinition beanDefinition = builder.getBeanDefinition();
            String beanName = "janytask-tconsumer#" + (cnt++);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    };

    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
//        queueManager.foreachProducerClassSet(producerPInvoker);
//        queueManager.foreachConsumerClassSet(consumerPInvoker);
//        // 使用Spring生成生产者和消费者
//        queueManager.setProducerCreator(new InstanceCreator() {
//            public Object createProducer(Class<?> _class) {
//                return beanFactory.getBean(_class);
//            }
//        });
//        queueManager.setConsumerCreator(new ConsumerCreator() {
//            public Object createConsumer(Class<?> consumerClass) {
//                return beanFactory.getBean(consumerClass);
//            }
//        });
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.registry = beanDefinitionRegistry;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, QueueProvider> beansOfType = beanFactory.getBeansOfType(QueueProvider.class);
        Iterator<QueueProvider> iterator = beansOfType.values().iterator();
        while (iterator.hasNext()) {
            this.addProvider(iterator.next());
        }
        this.start();
    }
}
