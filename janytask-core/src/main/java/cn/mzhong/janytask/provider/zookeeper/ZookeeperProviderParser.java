package cn.mzhong.janytask.provider.zookeeper;

import cn.mzhong.janytask.spring.ElementBeanDefinitionBuilder;
import cn.mzhong.janytask.spring.ProviderParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Zookeeper提供者解析器
 */
public class ZookeeperProviderParser extends ProviderParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ZookeeperProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, builder);
        builderPlus.parseStringPropertyFromAttr("connectString");
        builderPlus.parseStringPropertyFromAttr("rootPath");
        super.doParse(element, parserContext, builder);
    }
}
