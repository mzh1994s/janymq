package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.core.*;
import cn.mzhong.janytask.queue.loopline.LoopLineAnnotationHandler;
import cn.mzhong.janytask.queue.pipleline.PipleLineAnnotationHandler;
import cn.mzhong.janytask.queue.provider.QueueProvider;
import cn.mzhong.janytask.tool.PInvoker;
import cn.mzhong.janytask.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class QueueManager extends AbstractQueueManager {

    final static Logger Log = LoggerFactory.getLogger(QueueManager.class);

    public QueueManager(){
        // 注解处理器
        this.annotationHandlers.add(new PipleLineAnnotationHandler());
        this.annotationHandlers.add(new LoopLineAnnotationHandler());
    }

    /**
     * 根据提供商指示的package来扫描与此提供商相关联的生产者、消费者等类对象
     *
     * @param provider
     */
    private void initProvider(QueueProvider provider) {

    }

    /**
     * 支持多个提供商，需要扫描所有提供商
     *
     * @since 2.0.0
     */
    private void initProviders() {
        Iterator<QueueProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            initProvider(iterator.next());
        }
    }

    public void init() {
        // 初始化
        new QueueManager.ProducerInitializer().init();
        new QueueManager.ConsumerInitializer().init();
    }

    public Set<TaskExecutor> getTaskExecutors() {
        return null;
    }

    class ProducerInitializer implements TaskComponent {

        public void setContext(TaskContext context) {
        }

        protected void processProducer(TaskContext context, Class<?> producerClass) {
            // 处理生产者
            for (Method method : producerClass.getMethods()) {
                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
                while (iterator.hasNext()) {
                    QueueAnnotationHandler annotationProcessor = iterator.next();
                    Annotation annotation = method.getAnnotation(annotationProcessor.getAnnotationClass());
                    if (annotation != null) {
                        QueueInfo queueInfo = new QueueInfo<Annotation>(
                                annotation,
                                producerClass,
                                method,
                                null,
                                null,
                                null
                        );
                        // 注册messageDao
                        MessageDao messageDao = context.getQueueProvider().createMessageDao(queueInfo);
                        queueInfo.setMessageDao(messageDao);

                        // 映射Producer的MessageDao
                        methodMessageDaoMap.put(queueInfo.getProducerMethod(), messageDao);
                        //noinspection SingleStatementInBlock,unchecked
                        annotationProcessor.handleProducer(context, queueInfo);
                        if (Log.isDebugEnabled()) {
                            Log.debug("producer:'" + queueInfo.ID() + "'inited.");
                        }
                    }
                }
            }

        }

        @SuppressWarnings("unchecked")
        public void init() {
            QueueManager.this.foreachProducerClassSet(new PInvoker<Class<?>>() {
                public void invoke(Class<?> producerClass) throws Exception {
                    // 注册生产者代理
                    Object producer = producerCreator.createProducer(producerClass);
                    producerMap.put(producerClass, producer);
                    ProducerInitializer.this.processProducer(context, producerClass);
                }
            });
        }

    }

    class ConsumerInitializer implements TaskComponent {
        public void setContext(TaskContext context) {

        }

        /**
         * 消费者是提供者的实现，所以扫描消费者Class的接口，目的是找到提供者中的Pipleline、Loopline等注解
         *
         * @param consumerClass
         * @param method
         * @return
         */
        private <A extends Annotation> QueueInfo<A> findQueueInfo(Object consumer, Class<?> consumerClass, Method method, Class<A> annotationType) {
            Set<Class<?>> interfaces = ClassUtils.getInterfaces(consumerClass);
            QueueInfo<A> queueInfo = null;
            for (Class<?> _interface : interfaces) {
                try {
                    Method pMethod = _interface.getMethod(method.getName(), method.getParameterTypes());
                    if (pMethod != null) {
                        A annotation = pMethod.getAnnotation(annotationType);
                        if (annotation != null) {
                            queueInfo = new QueueInfo<A>(
                                    annotation,
                                    _interface,
                                    pMethod,
                                    consumer,
                                    consumerClass,
                                    method);
                            break;
                        }
                    }
                } catch (NoSuchMethodException e) {
                    // pass
                }
            }
            return queueInfo;
        }


        /**
         * 处理消费者
         *
         * @param context
         * @param consumer
         * @param consumerClass
         * @param <A>
         * @return
         */
        @SuppressWarnings("unchecked")
        protected <A extends Annotation> Set<TaskExecutor> handleConsumer(
                TaskContext context,
                Object consumer,
                Class<?> consumerClass) {
            Set<TaskExecutor> taskExecutos = new HashSet<TaskExecutor>();
            for (Method method : consumerClass.getMethods()) {
                Iterator<QueueAnnotationHandler> iterator = annotationHandlers.iterator();
                while (iterator.hasNext()) {
                    QueueAnnotationHandler annotationProcessor = iterator.next();
                    QueueInfo<A> queueInfo = findQueueInfo(
                            consumer,
                            consumerClass,
                            method,
                            annotationProcessor.getAnnotationClass());
                    if (queueInfo != null) {
                        // 注册messageDao
                        queueInfo.setMessageDao(context.getQueueProvider().createMessageDao(queueInfo));
                        // 创建消费者线程
                        taskExecutos.add(annotationProcessor.handleConsumer(context, queueInfo));
                        if (Log.isDebugEnabled()) {
                            Log.debug("consumer:'" + queueInfo.ID() + "'inited.");
                        }
                    }
                }
            }
            return taskExecutos;
        }

        /**
         * 创建线程列表（一个消费者消息队列一个线程）
         *
         * @return
         */

        @SuppressWarnings("unchecked")
        public void init() {
            final TaskWorker taskWorker = context.getTaskWorker();
            QueueManager.this.foreachConsumerClassSet(new PInvoker<Class<?>>() {
                public void invoke(Class<?> consumerClass) throws Exception {
                    Object consumer = consumerCreator.createConsumer(consumerClass);
                    QueueManager.this.consumerMap.put(consumerClass, consumer);
                    taskWorker.addExecutors(ConsumerInitializer.this.handleConsumer(context, consumer, consumerClass));
                }
            });
        }
    }

}


