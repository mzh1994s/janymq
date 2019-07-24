package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.util.PropertiesUtils;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 为了解决无法解析配置文件表达式（EL）的问题，2019年7月24日，将所有内部子Parser全部修改为继承AbstractSingleBeanDefinitionParser
 */
public class TaskNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * 读取 META-INF/janytask.spring.parsers配置文件，转换parser<br/>
     * 2019年7月24日将此方法移动到{@link cn.mzhong.janytask.spring.TaskNamespaceHandler}
     *
     * @return 将janytask.spring.parsers解析后的Parser表
     * @since 2.0.0
     */
    private static Map<String, AbstractSingleBeanDefinitionParser> getParses() {
        Map<String, AbstractSingleBeanDefinitionParser> parses = new HashMap<String, AbstractSingleBeanDefinitionParser>();
        Map<String, String> mergedProperties = PropertiesUtils.getMergedProperties("META-INF/janytask.spring.parsers");
        Iterator<Map.Entry<String, String>> iterator = mergedProperties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                Class<?> _class = Class.forName(entry.getValue());
                AbstractSingleBeanDefinitionParser parser =
                        (AbstractSingleBeanDefinitionParser) _class.getDeclaredConstructor().newInstance();
                parses.put(entry.getKey(), parser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return parses;
    }

    public void init() {
        Map<String, AbstractSingleBeanDefinitionParser> parsers = getParses();
        Iterator<Map.Entry<String, AbstractSingleBeanDefinitionParser>> iterator = parsers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AbstractSingleBeanDefinitionParser> entry = iterator.next();
            registerBeanDefinitionParser(entry.getKey(), entry.getValue());
        }
    }

}
