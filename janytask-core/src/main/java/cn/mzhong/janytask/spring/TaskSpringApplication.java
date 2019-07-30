package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.application.TaskApplication;
import cn.mzhong.janytask.queue.ConsumerFactory;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.ProducerFactory;
import cn.mzhong.janytask.queue.ProducerProxyFactory;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * Spring janytask应用程序
 */
public class TaskSpringApplication extends TaskApplication implements BeanDefinitionRegistryPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    protected BeanDefinitionRegistry registry;
    protected ConfigurableListableBeanFactory beanFactory;

    /**
     * 初始化生产者工厂
     */
    private void initProducerFactory() {
        // 使用Spring生成生产者和消费者
        this.queueManager.setProducerFactory(new ProducerFactory() {

            public void registryProducer(Class<?> _class, Map<Method, MessageDao> messageDaoMap) {
                // 此处使用Spring工厂模式创建生产者
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProducerProxyFactory.class);
                // 工厂方法为newInstance
                builder.setFactoryMethod("newInstance");
                // 工厂方法的两个参数
                builder.addConstructorArgValue(_class);
                builder.addConstructorArgValue(messageDaoMap);
                // 单例
                builder.setScope(BeanDefinition.SCOPE_SINGLETON);
                BeanDefinition beanDefinition = builder.getBeanDefinition();
                // 当前SpringContext下可能会有消费者和提供者两种实现，使用Autowired只能注入提供者
                // 即 提供者的Primary = true，消费者的 Primary = false.
                // 一般情况下不直接注入消费者，而是通过提供者发送消息间接调用消费者。
                // 当在一个应用中需要同步实现时可通过Resource或者Autowired + Qualifier的方式实现。
                beanDefinition.setPrimary(true);
                // 注册bean到Spring容器
                String beanName = BeanNames.generateBeanName(registry, "janytask-producer");
                registry.registerBeanDefinition(beanName, beanDefinition);
                // 先获取一次，否则使用Autowired注入的是Consumer而不是代理对象
                beanFactory.getBean(beanName);
            }

            public Object getObject(Class<?> _class) {
                return beanFactory.getBean(_class);
            }
        });
    }

    /**
     * 初始化消费者工厂
     */
    private void initConsumerFactory() {
        this.queueManager.setConsumerFactory(new ConsumerFactory() {

            public void registryConsumer(Class<?> _class) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(_class);
                builder.setScope(BeanDefinition.SCOPE_SINGLETON);
                BeanDefinition beanDefinition = builder.getBeanDefinition();
                beanDefinition.setPrimary(false);
                String beanName = BeanNames.generateBeanName(registry, "janytask-consumer");
                registry.registerBeanDefinition(beanName, beanDefinition);
            }

            public Object getObject(Class<?> _class) {
                return beanFactory.getBean(_class);
            }
        });
    }

    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        initProducerFactory();
        initConsumerFactory();
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.registry = beanDefinitionRegistry;
    }

    /**
     * 在SpringContext初始化完成后启动Janytask
     *
     * @param event
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, QueueProvider> beansOfType = beanFactory.getBeansOfType(QueueProvider.class);
        Iterator<QueueProvider> iterator = beansOfType.values().iterator();
        while (iterator.hasNext()) {
            this.addProvider(iterator.next());
        }
        this.start();
    }
}
