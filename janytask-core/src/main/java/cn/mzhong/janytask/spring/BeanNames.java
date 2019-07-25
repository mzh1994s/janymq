package cn.mzhong.janytask.spring;

public abstract class BeanNames {
    private BeanNames() {
    }

    public final static String BEAN_NAME_APPLICATION = "JanytaskApplicationBean";
    public final static String BEAN_NAME_QUEUE_PROVIDER = "JanytaskQueueProviderBean";
    public final static String BEAN_NAME_QUEUE_CONFIG = "JanytaskQueueConfigBean";
    public final static String BEAN_NAME_APPLICATION_CONFIG = "JanytaskApplicationConfigBean";
}
