package cn.mzhong.janytask.spring;

public abstract class BeanNames {
    private BeanNames() {
    }

    public final static String BEAN_NAME_APPLICATION = "onlyJanytaskApplication";
    public final static String BEAN_NAME_QUEUE_PROVIDER = "onlyJanytaskQueueProviderBean";
    public final static String BEAN_NAME_QUEUE_CONFIG = "onlyJanytaskQueueConfigBean";
    public final static String BEAN_NAME_APPLICATION_CONFIG = "onlyJanytaskApplicationConfigBean";
}
