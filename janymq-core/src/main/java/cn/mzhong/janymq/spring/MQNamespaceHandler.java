package cn.mzhong.janymq.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MQNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("application", new MQApplicationParser());
    }

}
