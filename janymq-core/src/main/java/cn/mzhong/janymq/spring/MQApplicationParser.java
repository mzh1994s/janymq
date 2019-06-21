package cn.mzhong.janymq.spring;

import cn.mzhong.janymq.config.ApplicationConfig;
import cn.mzhong.janymq.config.LooplineConfig;
import cn.mzhong.janymq.config.PiplelineConfig;
import cn.mzhong.janymq.redis.RedisLineManagerProvider;
import cn.mzhong.janymq.util.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.List;

/**
 * JSimpleMQ应用程序Bean解析器
 */
public class MQApplicationParser extends AbstractSingleBeanDefinitionParser {

    final static Logger Log = LoggerFactory.getLogger(MQApplicationParser.class);
    final static Class<?> _class = MQSpringApplication.class;

    /**
     * ID属性特殊处理
     *
     * @param element
     * @param builder
     * @return
     */
    protected String specialDoIdentifierAttribute(Element element, BeanDefinitionBuilder builder) {
        String id = element.getAttribute("id");
        if (id == null || id.length() == 0) {
            id = _class.getName();
            element.setAttribute("id", id);
        }
        return id;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return _class;
    }

    private BeanDefinition doParse(Element element, Class<?> _class, String initMethodName) {
        NamedNodeMap attributes = element.getAttributes();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(_class);
        List<Field> fieldList = FieldUtils.getAllFields(_class);
        // 简单转换，暂未实现递归
        for (Field field : fieldList) {
            String fieldName = field.getName();
            Node node = attributes.getNamedItem(fieldName);
            if (node != null) {
                Log.debug("{}.{}=>{}", element.getTagName(), fieldName, node.getNodeValue());
                builder.addPropertyValue(fieldName, node.getNodeValue());
            }
        }
        if (initMethodName != null) {
            builder.setInitMethodName(initMethodName);
        }
        return builder.getBeanDefinition();
    }

    private Element getFirstElementByName(Element element, String name) {
        NodeList nodeList = element.getElementsByTagNameNS("http://www.mzhong.cn/schema/janymq", name);
        if (nodeList.getLength() == 0) {
            return null;
        }
        Element configElement = (Element) nodeList.item(0);
        return configElement;
    }

    protected void doParseLineManagerProvider(Element root, BeanDefinitionBuilder builder) {
        String[] storeComponents = new String[]{"redis", "zookeeper"};
        Class<?>[] configClasses = new Class<?>[]{RedisLineManagerProvider.class, null};
        Element element = null;
        Class<?> _class = null;
        for (int i = 0; i < storeComponents.length; i++) {
            element = getFirstElementByName(root, "redis");
            if (element != null) {
                _class = configClasses[i];
                break;
            }
        }
        if (element != null && _class != null) {
            builder.addPropertyValue("lineManagerProvider", doParse(element, _class, "init"));
        }
    }

    protected void doParseApplicationConfig(Element root, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("applicationConfig", doParse(root, ApplicationConfig.class, null));
    }

    protected void doParsePiplelineConfig(Element root, BeanDefinitionBuilder builder) {
        Element element = getFirstElementByName(root, "pipleline");
        if (element != null) {
            builder.addPropertyValue("piplelineConfig", doParse(element, PiplelineConfig.class, null));
        }
    }

    protected void doParseLooplineConfig(Element root, BeanDefinitionBuilder builder) {
        Element element = getFirstElementByName(root, "loopline");
        if (element != null) {
            builder.addPropertyValue("looplineConfig", doParse(element, LooplineConfig.class, null));
        }
    }

    @Override
    protected void doParse(Element root, BeanDefinitionBuilder builder) {
        specialDoIdentifierAttribute(root, builder);
        doParseLineManagerProvider(root, builder);
        doParseApplicationConfig(root, builder);
        doParsePiplelineConfig(root, builder);
        doParseLooplineConfig(root, builder);
    }
}
