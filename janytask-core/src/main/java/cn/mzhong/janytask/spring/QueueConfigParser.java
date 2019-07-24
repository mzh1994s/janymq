package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.QueueConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

class QueueConfigParser extends ConfigParser {

    public QueueConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementToBeanDefinitionParser piplelineParser = new ElementToBeanDefinitionParser(element, QueueConfig.class);
        piplelineParser.parseStringPropertyFromAttr("cron");
        piplelineParser.parseStringPropertyFromAttr("zone");
        this.beanDefinitionBuilder.addPropertyValue("queueConfig", piplelineParser.getBeanDefinition());
    }
}
