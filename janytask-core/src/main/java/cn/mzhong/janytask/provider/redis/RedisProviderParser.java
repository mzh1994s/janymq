package cn.mzhong.janytask.provider.redis;

import cn.mzhong.janytask.spring.ElementBeanDefinitionBuilder;
import cn.mzhong.janytask.spring.ProviderParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * redis 提供者解析器
 */
public class RedisProviderParser extends ProviderParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return SpringRedisProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        ElementBeanDefinitionBuilder builderPlus = new ElementBeanDefinitionBuilder(element, builder);
        builderPlus.parseStringPropertyFromAttr("host");
        builderPlus.parseIntPropertyFromAttr("port");
        builderPlus.parseIntPropertyFromAttr("timeout");
        builderPlus.parseStringPropertyFromAttr("password");
        builderPlus.parseIntPropertyFromAttr("database");
        builderPlus.parseReferencePropertyFromAttr("jedisPool");
        builderPlus.parseReferencePropertyFromAttr("jedisPoolConfig");
        builderPlus.parseReferencePropertyFromAttr("connectionFactory");
        super.doParse(element, parserContext, builder);
    }
}
