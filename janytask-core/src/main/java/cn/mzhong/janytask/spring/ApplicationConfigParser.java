package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.ApplicationConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

/**
 * 应用程序配置解析器
 */
class ApplicationConfigParser extends ConfigParser {

    public ApplicationConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementToBeanDefinitionParser configParser = new ElementToBeanDefinitionParser(element, ApplicationConfig.class);
        configParser.parseStringPropertyFromAttr("basePackage");
        this.beanDefinitionBuilder.addPropertyValue("applicationConfig", configParser.getBeanDefinition());
    }
}
