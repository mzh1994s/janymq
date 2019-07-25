package cn.mzhong.janytask.queue.provider.any;

import cn.mzhong.janytask.queue.provider.ProviderParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 自定义提供者解析器
 */
public class AnyProviderParser extends ProviderParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return AnyProvider.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String ref = element.getAttribute("ref");
        builder.addPropertyReference("internalQueueProvider", ref);
    }
}
