package cn.mzhong.janymq.spring;

import cn.mzhong.janymq.config.ApplicationConfig;
import cn.mzhong.janymq.config.LooplineConfig;
import cn.mzhong.janymq.config.PiplelineConfig;
import cn.mzhong.janymq.redis.GenericRedisConnectionFactory;
import cn.mzhong.janymq.redis.RedisLineManagerProvider;
import cn.mzhong.janymq.redis.SpringRedisConnectionFactory;
import cn.mzhong.janymq.util.StringUtils;
import cn.mzhong.janymq.zookeeper.ZookeeperLineManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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

    private static Element getElementByName(Element element, String name) {
        NodeList nodeList = element.getElementsByTagNameNS("http://www.mzhong.cn/schema/janymq", name);
        if (nodeList.getLength() == 0) {
            return null;
        }
        Element configElement = (Element) nodeList.item(0);
        return configElement;
    }

    protected void doParseChildren(Element root, BeanDefinitionBuilder builder) {
        String[] storeComponents = new String[]{
                "conf-pipleline", "conf-loopline",
                "service-redis", "service-zookeeper"};
        Class<?>[] configClasses = new Class<?>[]{
                PiplelineConfigParser.class,
                LooplineConfigParser.class,
                RedisLineManagerProviderParser.class,
                ZookeeperLineManagerParser.class
        };
        for (int i = 0; i < storeComponents.length; i++) {
            Element element = getElementByName(root, storeComponents[i]);
            if (element != null) {
                try {
                    ConfigParser parser = (ConfigParser) configClasses[i].getConstructor(
                            Element.class, BeanDefinitionBuilder.class).newInstance(element, builder);
                    parser.doParser();
                } catch (Exception e) {
                    // pass
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
        specialDoIdentifierAttribute(root, builder);
        doParseChildren(root, builder);
        doParseApplicationConfig(root, builder);
    }
}

abstract class ConfigParser {
    protected Element element;
    protected BeanDefinitionBuilder beanDefinitionBuilder;

    public ConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        this.element = element;
        this.beanDefinitionBuilder = beanDefinitionBuilder;
    }

    public abstract void doParser();
}

class ApplicationConfigParser extends ConfigParser {

    public ApplicationConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementBeanDefinitionParser configParser = new ElementBeanDefinitionParser(element, ApplicationConfig.class);
        configParser.parseStringPropertyFromAttr("basePackage");
        this.beanDefinitionBuilder.addPropertyValue("applicationConfig", configParser.getBeanDefinition());
    }
}

class PiplelineConfigParser extends ConfigParser {

    public PiplelineConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementBeanDefinitionParser piplelineParser = new ElementBeanDefinitionParser(element, PiplelineConfig.class);
        piplelineParser.parseStringPropertyFromAttr("idleInterval");
        piplelineParser.parseStringPropertyFromAttr("sleepInterval");
        this.beanDefinitionBuilder.addPropertyValue("piplelineConfig", piplelineParser.getBeanDefinition());
    }
}


class LooplineConfigParser extends ConfigParser {

    public LooplineConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementBeanDefinitionParser piplelineParser = new ElementBeanDefinitionParser(element, LooplineConfig.class);
        piplelineParser.parseStringPropertyFromAttr("idleInterval");
        piplelineParser.parseStringPropertyFromAttr("sleepInterval");
        this.beanDefinitionBuilder.addPropertyValue("looplineConfig", piplelineParser.getBeanDefinition());
    }
}

class RedisLineManagerProviderParser extends ConfigParser {

    public RedisLineManagerProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    protected void doConnectionFactoryParser(String springConnectionFactoryRef) {
        // bean RedisConnectionFactory
        BeanDefinitionBuilder connectionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringRedisConnectionFactory.class);
        // 添加ref依赖规则
        connectionFactoryBuilder.addConstructorArgReference(springConnectionFactoryRef);
        this.registerLineManagerProvider(connectionFactoryBuilder);
    }

    protected void doJedisPoolParser(String jedisPoolRef) {
        // bean RedisConnectionFactory
        BeanDefinitionBuilder connectionFactoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(GenericRedisConnectionFactory.class);
        // 添加ref依赖规则
        connectionFactoryBuilder.addConstructorArgReference(jedisPoolRef);
        this.registerLineManagerProvider(connectionFactoryBuilder);
    }

    protected void doJedisPoolConfigParser(String jedisPoolConfigRef) {
        ElementBeanDefinitionParser jedisPoolParser = new ElementBeanDefinitionParser(element, JedisPool.class);
        // public JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
        jedisPoolParser.addConstructorArgReference(jedisPoolConfigRef);
        jedisPoolParser.parseStringConstructorFromAttr("hostName");
        jedisPoolParser.parseIntConstructorFromAttr("port");
        jedisPoolParser.parseIntConstructorFromAttr("timeout");
        jedisPoolParser.parseIntConstructorFromAttr("password");
        jedisPoolParser.parseIntConstructorFromAttr("database");
        this.registerConnectionFactory(jedisPoolParser.getBeanDefinition());
    }

    protected void doHostPortParser() {
        BeanDefinitionBuilder jedisPoolConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(JedisPoolConfig.class);
        ElementBeanDefinitionParser jedisPoolParser = new ElementBeanDefinitionParser(element, JedisPool.class);
        // public JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
        jedisPoolParser.addConstructorArgValue(jedisPoolConfigBuilder.getBeanDefinition());
        jedisPoolParser.parseStringConstructorFromAttr("hostName");
        jedisPoolParser.parseIntConstructorFromAttr("port");
        jedisPoolParser.parseIntConstructorFromAttr("timeout");
        jedisPoolParser.parseIntConstructorFromAttr("password");
        jedisPoolParser.parseIntConstructorFromAttr("database");
        this.registerConnectionFactory(jedisPoolParser.getBeanDefinition());
    }

    protected void registerConnectionFactory(BeanDefinition jedisPoolBeanDefinition) {
        // bean RedisConnectionFactory
        BeanDefinitionBuilder connectionFactoryBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(GenericRedisConnectionFactory.class);
        connectionFactoryBuilder.addConstructorArgValue(jedisPoolBeanDefinition);
        this.registerLineManagerProvider(connectionFactoryBuilder);
    }

    protected void registerLineManagerProvider(BeanDefinitionBuilder connectionFactoryBuilder) {
        // bean RedisLineManagerProvider
        ElementBeanDefinitionParser redisLineManagerProviderParser = new ElementBeanDefinitionParser(element, RedisLineManagerProvider.class);
        redisLineManagerProviderParser.addPropertyValue("connectionFactory", connectionFactoryBuilder.getBeanDefinition());
        redisLineManagerProviderParser.parseStringPropertyFromAttr("rootPath");
        this.beanDefinitionBuilder.addPropertyValue("lineManagerProvider", redisLineManagerProviderParser.getBeanDefinition());
    }

    @Override
    public void doParser() {
        String springConnectionFactoryRef = element.getAttribute("connectionFactory-ref");
        // 使用Spring连接, 编写依赖规则
        if (!StringUtils.isEmpty(springConnectionFactoryRef)) {
            doConnectionFactoryParser(springConnectionFactoryRef);
        } else {
            String jedisPoolRef = element.getAttribute("jedisPool-ref");
            if (!StringUtils.isEmpty(jedisPoolRef)) {
                doJedisPoolParser(jedisPoolRef);
            } else {
                String jedisPoolConfigRef = element.getAttribute("jedisPoolConfig-ref");
                if (!StringUtils.isEmpty(jedisPoolConfigRef)) {
                    doJedisPoolConfigParser(jedisPoolConfigRef);
                } else {
                    doHostPortParser();
                }
            }
        }
    }
}

class ZookeeperLineManagerParser extends ConfigParser {

    public ZookeeperLineManagerParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        // bean ZookeeperLineManagerProvider
        ElementBeanDefinitionParser elementBeanDefinitionParser = new ElementBeanDefinitionParser(element, ZookeeperLineManagerProvider.class);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("connectString");
        elementBeanDefinitionParser.parseStringPropertyFromAttr("rootPath");
        this.beanDefinitionBuilder.addPropertyValue("lineManagerProvider", elementBeanDefinitionParser.getBeanDefinition());
    }
}
