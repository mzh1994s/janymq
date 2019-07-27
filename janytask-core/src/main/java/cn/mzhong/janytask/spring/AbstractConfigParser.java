package cn.mzhong.janytask.spring;


import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public abstract class AbstractConfigParser extends AbstractSingleBeanDefinitionParser {

    protected abstract String beanName();

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        int cnt = 0;
        String beanName = beanName();
        while (parserContext.getRegistry().containsBeanDefinition(beanName)) {
            beanName = beanName + "#" + (cnt++);
        }
        element.setAttribute("id", beanName());
        return super.resolveId(element, definition, parserContext);
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
    }
}
