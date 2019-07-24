package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.QueueConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

class QueueConfigParser extends AbstractConfigParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return QueueConfig.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, builder);
        builderPlus.parseStringPropertyFromAttr("cron");
        builderPlus.parseStringPropertyFromAttr("zone");
        super.doParse(element, parserContext, builder);
    }

    protected String beanName() {
        return BeanNames.BEAN_NAME_QUEUE_CONFIG;
    }
}
