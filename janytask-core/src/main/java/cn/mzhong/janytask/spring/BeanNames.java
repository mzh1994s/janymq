package cn.mzhong.janytask.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public abstract class BeanNames {
    private BeanNames() {
    }

    public final static String BEAN_NAME_APPLICATION = "JanytaskApplicationBean";
    public final static String BEAN_NAME_QUEUE_PROVIDER = "JanytaskQueueProviderBean";
    public final static String BEAN_NAME_QUEUE_CONFIG = "JanytaskQueueConfigBean";
    public final static String BEAN_NAME_APPLICATION_CONFIG = "JanytaskApplicationConfigBean";

    /**
     * 获取Bean的名称，从参数给定基础名称，当Spring上下文存在同名Bean时从基础名称后面加序号区分
     *
     * @param registry
     * @param baseName
     * @return
     */
    public static String generateBeanName(BeanDefinitionRegistry registry, String baseName) {
        int cnt = 0;
        String beanName;
        do {
            beanName = baseName + "#" + (cnt++);
        } while (registry.containsBeanDefinition(beanName));
        return beanName;
    }
}
