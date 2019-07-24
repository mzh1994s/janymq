package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.QueueConfig;
import cn.mzhong.janytask.provider.jdbc.JdbcProviderParser;
import cn.mzhong.janytask.provider.redis.RedisProviderParser;
import cn.mzhong.janytask.provider.zookeeper.ZookeeperProviderParser;
import cn.mzhong.janytask.util.PropertiesUtils;
import cn.mzhong.janytask.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JSimpleMQ应用程序Bean解析器
 */
public class ApplicationParser extends AbstractSingleBeanDefinitionParser {

    final static Logger Log = LoggerFactory.getLogger(ApplicationParser.class);
    final static Class<?> _class = TaskSpringApplication.class;

    /**
     * ID属性特殊处理
     *
     * @param element
     * @param builder
     * @return
     */
    protected String doSpecialIDAttribute(Element element, BeanDefinitionBuilder builder) {
        String id = element.getAttribute("id");
        if (StringUtils.isEmpty(id)) {
            id = _class.getName();
            element.setAttribute("id", id);
        }
        return id;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return _class;
    }

    private static Element getElementByName(Element element, String name) {
        NodeList nodeList = element.getElementsByTagNameNS("http://www.mzhong.cn/schema/janytask", name);
        if (nodeList.getLength() == 0) {
            return null;
        }
        Element configElement = (Element) nodeList.item(0);
        return configElement;
    }

    /**
     * 读取 META-INF/janytask.spring.parsers配置文件，转换parser
     *
     * @return
     */
    private static Map<String, Class<?>> getParses() {
        Map<String, Class<?>> parses = new HashMap<String, Class<?>>();
        Map<String, String> mergedProperties = PropertiesUtils.getMergedProperties("META-INF/janytask.spring.parsers");
        Iterator<Map.Entry<String, String>> iterator = mergedProperties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                parses.put(entry.getKey(), Class.forName(entry.getValue()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return parses;
    }

    protected void doParseChildren(Element root, BeanDefinitionBuilder builder) {
        Map<String, Class<?>> parsers = getParses();
        Iterator<Map.Entry<String, Class<?>>> iterator = parsers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Class<?>> parserEntry = iterator.next();
            Element element = getElementByName(root, parserEntry.getKey());
            if (element != null) {
                try {
                    ConfigParser parser = (ConfigParser) parserEntry.getValue().getConstructor(
                            Element.class, BeanDefinitionBuilder.class).newInstance(element, builder);
                    parser.doParser();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void doParseApplicationConfig(Element root, BeanDefinitionBuilder builder) {
        ApplicationConfigParser applicationParser = new ApplicationConfigParser(root, builder);
        applicationParser.doParser();
    }

    @Override
    protected void doParse(Element root, BeanDefinitionBuilder builder) {
        doSpecialIDAttribute(root, builder);
        doParseChildren(root, builder);
        doParseApplicationConfig(root, builder);
    }
}

