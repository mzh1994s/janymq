package cn.mzhong.janymq.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ElementToBeanDefinitionParser {
    protected Element element;
    protected BeanDefinitionBuilder beanDefinitionBuilder;

    public ElementToBeanDefinitionParser(Element element, Class<?> _class) {
        this.element = element;
        this.beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(_class);
    }

    public String getStringFromAttr(String name) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name);
        }
        return null;
    }

    public int getIntFromAttr(String name) {
        if (element.hasAttribute(name)) {
            return Integer.parseInt(element.getAttribute(name));
        }
        return 0;
    }

    public ElementToBeanDefinitionParser parseStringConstructorFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addConstructorArgValue(getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementToBeanDefinitionParser parseIntConstructorFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addConstructorArgValue(getIntFromAttr(attributeName));
        }
        return this;
    }

    public ElementToBeanDefinitionParser parseStringPropertyFromAttr(String attributeName) {
        return parseStringPropertyFromAttr(attributeName, attributeName);
    }

    public ElementToBeanDefinitionParser parseStringPropertyFromAttr(String attributeName, String property) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyValue(property, getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementToBeanDefinitionParser parseIntPropertyFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyValue(attributeName, getIntFromAttr(attributeName));
        }
        return this;
    }

    public ElementToBeanDefinitionParser parseReferencePropertyFromAttr(String property) {
        return parseReferencePropertyFromAttr(property + "-ref", property);
    }

    public ElementToBeanDefinitionParser parseReferencePropertyFromAttr(String attributeName, String property) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyReference(property, getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementToBeanDefinitionParser addConstructorArgValue(Object value) {
        this.beanDefinitionBuilder.addConstructorArgValue(value);
        return this;
    }

    public ElementToBeanDefinitionParser addConstructorArgReference(String ref) {
        this.beanDefinitionBuilder.addConstructorArgReference(ref);
        return this;
    }

    public ElementToBeanDefinitionParser addPropertyValue(String name, Object value) {
        this.beanDefinitionBuilder.addPropertyValue(name, value);
        return this;
    }

    public ElementToBeanDefinitionParser addPropertyReference(String name, String value) {
        this.beanDefinitionBuilder.addPropertyReference(name, value);
        return this;
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinitionBuilder.getBeanDefinition();
    }
}
