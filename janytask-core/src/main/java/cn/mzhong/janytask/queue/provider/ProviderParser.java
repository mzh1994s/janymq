package cn.mzhong.janytask.queue.provider;

import cn.mzhong.janytask.spring.AbstractParser;
import cn.mzhong.janytask.spring.BeanNames;
import cn.mzhong.janytask.spring.ElementBeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public abstract class ProviderParser extends AbstractParser {

    final protected String beanName() {
        return BeanNames.BEAN_NAME_QUEUE_PROVIDER;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, builder);
        builderPlus.parseStringPropertyFromAttr("package");
        super.doParse(element, parserContext, builder);
    }
}
