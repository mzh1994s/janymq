package cn.mzhong.janytask.application;

import cn.mzhong.janytask.spring.AbstractParser;
import cn.mzhong.janytask.spring.BeanNames;
import cn.mzhong.janytask.spring.ElementBeanDefinitionBuilder;
import cn.mzhong.janytask.spring.TaskSpringApplication;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 应用程序Bean解析器
 */
public class SpringApplicationParser extends AbstractParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return TaskSpringApplication.class;
    }

    protected String beanName() {
        return BeanNames.BEAN_NAME_APPLICATION;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, builder);
        builderPlus.parseStringPropertyFromAttr("name");
    }
}

