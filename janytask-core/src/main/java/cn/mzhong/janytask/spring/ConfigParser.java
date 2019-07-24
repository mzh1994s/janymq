package cn.mzhong.janytask.spring;


import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public abstract class ConfigParser {
    protected Element element;
    protected BeanDefinitionBuilder beanDefinitionBuilder;

    public ConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        this.element = element;
        this.beanDefinitionBuilder = beanDefinitionBuilder;
    }

    public abstract void doParser();
}
