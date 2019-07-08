package cn.mzhong.janytask.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class TaskNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("application", new ApplicationParser());
    }

}
