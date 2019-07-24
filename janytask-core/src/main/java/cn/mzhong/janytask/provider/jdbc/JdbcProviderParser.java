package cn.mzhong.janytask.provider.jdbc;

import cn.mzhong.janytask.spring.ElementBeanDefinitionBuilder;
import cn.mzhong.janytask.spring.ProviderParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JdbcProviderParser extends ProviderParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder elementBeanDefinitionParser = new ElementBeanDefinitionBuilder(element, builder);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("table");
        elementBeanDefinitionParser.parseReferencePropertyFromAttr("dataSource");
        super.doParse(element, parserContext, builder);
    }
}
