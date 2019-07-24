package cn.mzhong.janytask.provider.jdbc;

import cn.mzhong.janytask.spring.ConfigParser;
import cn.mzhong.janytask.spring.ElementToBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class JdbcProviderParser extends ConfigParser {

    public JdbcProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        // bean JdbcProvider
        ElementToBeanDefinitionParser elementBeanDefinitionParser = new ElementToBeanDefinitionParser(element, JdbcProvider.class);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("table");
        elementBeanDefinitionParser.parseReferencePropertyFromAttr("dataSource");
        this.beanDefinitionBuilder.addPropertyValue("queueProvider", elementBeanDefinitionParser.getBeanDefinition());
    }
}
