package cn.mzhong.janymq.test;

import cn.mzhong.janymq.producer.TestMQ;

import java.lang.reflect.Proxy;

public class ProxyMain {

    public static void main(String[] args) {

        Class<?> proxyClass = Proxy.getProxyClass(TestMQ.class.getClassLoader(), TestMQ.class);
        try {
            Class.forName(proxyClass.getName());
            System.out.println(proxyClass.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
