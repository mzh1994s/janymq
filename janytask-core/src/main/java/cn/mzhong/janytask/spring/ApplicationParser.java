package cn.mzhong.janytask.spring;

import cn.mzhong.janytask.config.ApplicationConfig;
import cn.mzhong.janytask.config.LooplineConfig;
import cn.mzhong.janytask.config.PiplelineConfig;
import cn.mzhong.janytask.jdbc.JdbcProvider;
import cn.mzhong.janytask.redis.GenericRedisConnectionFactory;
import cn.mzhong.janytask.redis.RedisProvider;
import cn.mzhong.janytask.redis.SpringRedisConnectionFactory;
import cn.mzhong.janytask.util.StringUtils;
import cn.mzhong.janytask.zookeeper.ZookeeperLineManagerProvider;
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
        NodeList nodeList = element.getElementsByTagNameNS("http://www.mzhong.cn/schema/janytask", name);
        if (nodeList.getLength() == 0) {
            return null;
        }
        Element configElement = (Element) nodeList.item(0);
        return configElement;
    }

    protected void doParseChildren(Element root, BeanDefinitionBuilder builder) {
        String[] elementNames = new String[]{
                "conf-pipleline", "conf-loopline",
                "provider-any", "provider-redis", "provider-zookeeper", "provider-jdbc"};
        Class<?>[] configClasses = new Class<?>[]{
                PiplelineConfigParser.class,
                LooplineConfigParser.class,
                AnyLineManagerProviderParser.class,
                RedisProviderParser.class,
                ZookeeperProviderParser.class,
                JdbcProviderParser.class
        };
        for (int i = 0; i < elementNames.length; i++) {
            Element element = getElementByName(root, elementNames[i]);
            if (element != null) {
                try {
                    ConfigParser parser = (ConfigParser) configClasses[i].getConstructor(
                            Element.class, BeanDefinitionBuilder.class).newInstance(element, builder);
                    parser.doParser();
                } catch (Exception e) {
                    Log.error("解析配置出错：" + elementNames[i], e);
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
        doSpecialIDAttribute(root, builder);
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

/**
 * 应用程序配置解析器
 */
class ApplicationConfigParser extends ConfigParser {

    public ApplicationConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementToBeanDefinitionParser configParser = new ElementToBeanDefinitionParser(element, ApplicationConfig.class);
        configParser.parseStringPropertyFromAttr("basePackage");
        this.beanDefinitionBuilder.addPropertyValue("applicationConfig", configParser.getBeanDefinition());
    }
}

/**
 * Pipleline配置解析器
 */
class PiplelineConfigParser extends ConfigParser {

    public PiplelineConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementToBeanDefinitionParser piplelineParser = new ElementToBeanDefinitionParser(element, PiplelineConfig.class);
        piplelineParser.parseStringPropertyFromAttr("idleInterval");
        piplelineParser.parseStringPropertyFromAttr("sleepInterval");
        this.beanDefinitionBuilder.addPropertyValue("piplelineConfig", piplelineParser.getBeanDefinition());
    }
}

/**
 * loopline配置解析器
 */
class LooplineConfigParser extends ConfigParser {

    public LooplineConfigParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        ElementToBeanDefinitionParser piplelineParser = new ElementToBeanDefinitionParser(element, LooplineConfig.class);
        piplelineParser.parseStringPropertyFromAttr("idleInterval");
        piplelineParser.parseStringPropertyFromAttr("sleepInterval");
        this.beanDefinitionBuilder.addPropertyValue("looplineConfig", piplelineParser.getBeanDefinition());
    }
}

/**
 * 自定义提供者解析器
 */
class AnyLineManagerProviderParser extends ConfigParser {

    public AnyLineManagerProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        this.beanDefinitionBuilder.addPropertyReference("lineManagerProvider", element.getAttribute("ref"));
    }
}

/**
 * redis 提供者解析器
 */
class RedisProviderParser extends ConfigParser {

    public RedisProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
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
        ElementToBeanDefinitionParser jedisPoolParser = new ElementToBeanDefinitionParser(element, JedisPool.class);
        // public JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
        jedisPoolParser.addConstructorArgReference(jedisPoolConfigRef);
        jedisPoolParser.parseStringConstructorFromAttr("host");
        jedisPoolParser.parseIntConstructorFromAttr("port");
        jedisPoolParser.parseIntConstructorFromAttr("timeout");
        jedisPoolParser.parseIntConstructorFromAttr("password");
        jedisPoolParser.parseIntConstructorFromAttr("database");
        this.registerConnectionFactory(jedisPoolParser.getBeanDefinition());
    }

    protected void doHostPortParser() {
        BeanDefinitionBuilder jedisPoolConfigBuilder = BeanDefinitionBuilder.genericBeanDefinition(JedisPoolConfig.class);
        ElementToBeanDefinitionParser jedisPoolParser = new ElementToBeanDefinitionParser(element, JedisPool.class);
        // public JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
        jedisPoolParser.addConstructorArgValue(jedisPoolConfigBuilder.getBeanDefinition());
        jedisPoolParser.parseStringConstructorFromAttr("host");
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
        // bean RedisProvider
        ElementToBeanDefinitionParser redisLineManagerProviderParser = new ElementToBeanDefinitionParser(element, RedisProvider.class);
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

/**
 * Zookeeper提供者解析器
 */
class ZookeeperProviderParser extends ConfigParser {

    public ZookeeperProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        // bean ZookeeperLineManagerProvider
        ElementToBeanDefinitionParser elementBeanDefinitionParser = new ElementToBeanDefinitionParser(element, ZookeeperLineManagerProvider.class);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("connectString");
        elementBeanDefinitionParser.parseStringPropertyFromAttr("rootPath");
        this.beanDefinitionBuilder.addPropertyValue("lineManagerProvider", elementBeanDefinitionParser.getBeanDefinition());
    }
}

class JdbcProviderParser extends ConfigParser {

    public JdbcProviderParser(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        super(element, beanDefinitionBuilder);
    }

    @Override
    public void doParser() {
        // bean JdbcProvider
        ElementToBeanDefinitionParser elementBeanDefinitionParser = new ElementToBeanDefinitionParser(element, JdbcProvider.class);
        elementBeanDefinitionParser.parseStringPropertyFromAttr("table");
        elementBeanDefinitionParser.parseReferencePropertyFromAttr("dataSource");
        this.beanDefinitionBuilder.addPropertyValue("lineManagerProvider", elementBeanDefinitionParser.getBeanDefinition());
    }
}
