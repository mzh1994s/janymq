package cn.mzhong.janytask.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

/**
 * 自定义提供者解析器
 */
class AnyProviderParser extends ConfigParser {

    public AnyProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        this.beanDefinitionBuilder.addPropertyReference("queueProvider", element.getAttribute("ref"));
    }
}
