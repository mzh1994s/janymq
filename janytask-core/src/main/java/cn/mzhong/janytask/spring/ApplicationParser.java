package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.ApplicationConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 应用程序Bean解析器
 */
public class ApplicationParser extends AbstractConfigParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return TaskSpringApplication.class;
    }

    protected String beanName() {
        return BeanNames.BEAN_NAME_APPLICATION;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinitionBuilder configBuilder = BeanDefinitionBuilder.genericBeanDefinition(ApplicationConfig.class);
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, configBuilder);
        builderPlus.parseStringPropertyFromAttr("basePackage");
        parserContext.getRegistry().registerBeanDefinition(BeanNames.BEAN_NAME_APPLICATION_CONFIG, builderPlus.getBeanDefinition());
    }
}

