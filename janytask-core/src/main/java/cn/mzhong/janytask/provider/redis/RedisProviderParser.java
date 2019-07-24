package cn.mzhong.janytask.provider.redis;

import cn.mzhong.janytask.spring.ConfigParser;
import cn.mzhong.janytask.spring.ElementToBeanDefinitionParser;
import cn.mzhong.janytask.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis 提供者解析器
 */
public class RedisProviderParser extends ConfigParser {

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
        this.beanDefinitionBuilder.addPropertyValue("queueProvider", redisLineManagerProviderParser.getBeanDefinition());
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
