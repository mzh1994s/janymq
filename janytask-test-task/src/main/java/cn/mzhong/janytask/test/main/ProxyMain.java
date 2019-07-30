package cn.mzhong.janytask.test.main;

import cn.mzhong.janytask.test.redis.producer.RedisTaskTask;

import java.lang.reflect.Proxy;

public class ProxyMain {

    public static void main(String[] args) {

        Class<?> proxyClass = Proxy.getProxyClass(RedisTaskTask.class.getClassLoader(), RedisTaskTask.class);
        try {
            Class.forName(proxyClass.getName());
            System.out.println(proxyClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
