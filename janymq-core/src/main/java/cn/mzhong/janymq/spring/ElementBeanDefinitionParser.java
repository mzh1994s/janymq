package cn.mzhong.janymq.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public class ElementBeanDefinitionParser {
    protected Element element;
    protected BeanDefinitionBuilder beanDefinitionBuilder;

    public ElementBeanDefinitionParser(Element element, Class<?> _class) {
        this.element = element;
        this.beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(_class);
    }

    public String getStringFromAttr(String name){
        if(element.hasAttribute(name)){
            return element.getAttribute(name);
        }
        return null;
    }

    public int getIntFromAttr(String name){
        if(element.hasAttribute(name)){
            return Integer.parseInt(element.getAttribute(name));
        }
        return 0;
    }

    public ElementBeanDefinitionParser parseStringConstructorFromAttr(String name){
        if(element.hasAttribute(name)){
            this.beanDefinitionBuilder.addConstructorArgValue(getStringFromAttr(name));
        }
        return this;
    }
    public ElementBeanDefinitionParser parseIntConstructorFromAttr(String name){
        if(element.hasAttribute(name)){
            this.beanDefinitionBuilder.addConstructorArgValue(getIntFromAttr(name));
        }
        return this;
    }

    public ElementBeanDefinitionParser parseStringPropertyFromAttr(String name){
        if(element.hasAttribute(name)){
            this.beanDefinitionBuilder.addPropertyValue(name, getStringFromAttr(name));
        }
        return this;
    }

    public ElementBeanDefinitionParser parseIntPropertyFromAttr(String name){
        if(element.hasAttribute(name)){
            this.beanDefinitionBuilder.addPropertyValue(name, getIntFromAttr(name));
        }
        return this;
    }
    public ElementBeanDefinitionParser addConstructorArgValue(Object value){
        this.beanDefinitionBuilder.addConstructorArgValue(value);
        return this;
    }
    public ElementBeanDefinitionParser addConstructorArgReference(String ref){
        this.beanDefinitionBuilder.addConstructorArgReference(ref);
        return this;
    }
    public ElementBeanDefinitionParser addPropertyValue(String name, Object value){
        this.beanDefinitionBuilder.addPropertyValue(name, value);
        return this;
    }

    public BeanDefinition getBeanDefinition(){
        return this.beanDefinitionBuilder.getBeanDefinition();
    }
}
