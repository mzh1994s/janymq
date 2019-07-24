package cn.mzhong.janytask.provider.zookeeper;

import cn.mzhong.janytask.spring.ConfigParser;
import cn.mzhong.janytask.spring.ElementToBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

/**
 * Zookeeper提供者解析器
 */
public class ZookeeperProviderParser extends ConfigParser {

    public ZookeeperProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        // bean ZookeeperProvider
        ElementToBeanDefinitionParser elementBeanDefinitionParser = new ElementToBeanDefinitionParser(element, ZookeeperProvider.class);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("connectString");
        elementBeanDefinitionParser.parseStringPropertyFromAttr("rootPath");
        this.beanDefinitionBuilder.addPropertyValue("queueProvider", elementBeanDefinitionParser.getBeanDefinition());
    }
}
