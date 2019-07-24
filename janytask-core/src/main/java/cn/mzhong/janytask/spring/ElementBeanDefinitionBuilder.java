package cn.mzhong.janytask.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ElementBeanDefinitionBuilder {
    protected Element element;
    protected BeanDefinitionBuilder beanDefinitionBuilder;

    public ElementBeanDefinitionBuilder(Element element, BeanDefinitionBuilder beanDefinitionBuilder) {
        this.element = element;
        this.beanDefinitionBuilder = beanDefinitionBuilder;
    }

    public String getStringFromAttr(String name) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name).trim();
        }
        return null;
    }

    public Integer getIntegerFromAttr(String name){
        if (element.hasAttribute(name)) {
            return Integer.parseInt(element.getAttribute(name).trim());
        }
        return null;
    }

    public ElementBeanDefinitionBuilder parseStringConstructorFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addConstructorArgValue(getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementBeanDefinitionBuilder parseIntConstructorFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addConstructorArgValue(getIntegerFromAttr(attributeName));
        }
        return this;
    }

    public ElementBeanDefinitionBuilder parseStringPropertyFromAttr(String attributeName) {
        return parseStringPropertyFromAttr(attributeName, attributeName);
    }

    public ElementBeanDefinitionBuilder parseStringPropertyFromAttr(String attributeName, String property) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyValue(property, getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementBeanDefinitionBuilder parseIntPropertyFromAttr(String attributeName) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyValue(attributeName, getIntegerFromAttr(attributeName));
        }
        return this;
    }

    public ElementBeanDefinitionBuilder parseReferencePropertyFromAttr(String property) {
        return parseReferencePropertyFromAttr(property + "-ref", property);
    }

    public ElementBeanDefinitionBuilder parseReferencePropertyFromAttr(String attributeName, String property) {
        if (element.hasAttribute(attributeName)) {
            this.beanDefinitionBuilder.addPropertyReference(property, getStringFromAttr(attributeName));
        }
        return this;
    }

    public ElementBeanDefinitionBuilder addConstructorArgValue(Object value) {
        this.beanDefinitionBuilder.addConstructorArgValue(value);
        return this;
    }

    public ElementBeanDefinitionBuilder addConstructorArgReference(String ref) {
        this.beanDefinitionBuilder.addConstructorArgReference(ref);
        return this;
    }

    public ElementBeanDefinitionBuilder addPropertyValue(String name, Object value) {
        this.beanDefinitionBuilder.addPropertyValue(name, value);
        return this;
    }

    public ElementBeanDefinitionBuilder addPropertyReference(String name, String value) {
        this.beanDefinitionBuilder.addPropertyReference(name, value);
        return this;
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinitionBuilder.getBeanDefinition();
    }
}
