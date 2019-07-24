package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.exception.DuplicateProviderException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.dao.DuplicateKeyException;
import org.w3c.dom.Element;

public abstract class ProviderParser extends AbstractConfigParser {

    final protected String beanName() {
        return BeanNames.BEAN_NAME_QUEUE_PROVIDER;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        if (parserContext.getRegistry().containsBeanDefinition(BeanNames.BEAN_NAME_QUEUE_PROVIDER)) {
            throw new DuplicateProviderException("你在Spring中提供了重复的提供商，Janytask暂不支持多个提供商同时工作，如果你配置了多个类似于<janytask:provider-xxx …… />的配置项，请确认你需要哪一个！");
        }
        super.doParse(element, parserContext, builder);
    }
}
