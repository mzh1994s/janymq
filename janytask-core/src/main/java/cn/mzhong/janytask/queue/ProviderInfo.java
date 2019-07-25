package cn.mzhong.janytask.queue;

import cn.mzhong.janytask.queue.provider.QueueProvider;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

public class ProviderInfo implements Serializable {
    protected QueueProvider provider;
    protected Map<Method, MessageDao> methodMessageDaoMap;

    public QueueProvider getProvider() {
        return provider;
    }

    public void setProvider(QueueProvider provider) {
        this.provider = provider;
    }

    public Map<Method, MessageDao> getMethodMessageDaoMap() {
        return methodMessageDaoMap;
    }

    public void setMethodMessageDaoMap(Map<Method, MessageDao> methodMessageDaoMap) {
        this.methodMessageDaoMap = methodMessageDaoMap;
    }
}
