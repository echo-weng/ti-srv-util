package me.andpay.ti.spring;

import java.util.List;

import me.andpay.ti.util.StringUtil;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * SpringBean工具类。
 * 
 * @author sea.bao
 */
public class SpringBeanUtils {
	static final String REF_ATTRIBUTE = "ref";
	static final String METHOD_ATTRIBUTE = "method";
	static final String ORDER = "order";

	public static boolean initBeanIfNecessary(Object bean) {
		if (bean instanceof InitializingBean) {
			InitializingBean initBean = (InitializingBean) bean;
			try {
				initBean.afterPropertiesSet();
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		return false;
	}

	/**
	 * Populates the specified bean definition property with the value of the
	 * attribute whose name is provided if that attribute is defined in the
	 * given element.
	 * 
	 * @param bean
	 *            the bean to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used to populate
	 *            the property
	 * @param propertyName
	 *            the name of the property to be populated
	 */
	public static boolean setValueIfAttributeDefined(PropertiesHolder holder, Object bean, Element element,
			String attributeName, String propertyName) {
		String attributeValue = element.getAttribute(attributeName);
		if (StringUtils.hasText(attributeValue)) {
			holder.setProperty(bean, propertyName, StringUtil.emptyAsNull(attributeValue));
			return true;
		}
		return false;
	}

	/**
	 * Populates the bean definition property corresponding to the specified
	 * attributeName with the value of that attribute if it is defined in the
	 * given element.
	 * 
	 * <p>
	 * The property name will be the camel-case equivalent of the lower case
	 * hyphen separated attribute (e.g. the "foo-bar" attribute would match the
	 * "fooBar" property).
	 * 
	 * @see Conventions#attributeNameToPropertyName(String)
	 * 
	 * @param bean
	 *            the bean to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be set on the
	 *            property
	 */
	public static boolean setValueIfAttributeDefined(PropertiesHolder holder, Object bean, Element element,
			String attributeName) {
		return setValueIfAttributeDefined(holder, bean, element, attributeName,
				Conventions.attributeNameToPropertyName(attributeName));
	}

	public static boolean setValueIfAttributeDefined(PropertiesHolder holder, Object bean, String prefix,
			Element element, String attributeName) {
		return setValueIfAttributeDefined(holder, bean, element, attributeName,
				prefix + Conventions.attributeNameToPropertyName(attributeName));
	}

	/**
	 * Populates the specified bean definition property with the value of the
	 * attribute whose name is provided if that attribute is defined in the
	 * given element.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used to populate
	 *            the property
	 * @param propertyName
	 *            the name of the property to be populated
	 */
	public static boolean setValueIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName, String propertyName) {
		String attributeValue = element.getAttribute(attributeName);
		if (StringUtils.hasText(attributeValue)) {
			builder.addPropertyValue(propertyName, new TypedStringValue(attributeValue));
			return true;
		}
		return false;
	}

	/**
	 * Populates the bean definition property corresponding to the specified
	 * attributeName with the value of that attribute if it is defined in the
	 * given element.
	 * 
	 * <p>
	 * The property name will be the camel-case equivalent of the lower case
	 * hyphen separated attribute (e.g. the "foo-bar" attribute would match the
	 * "fooBar" property).
	 * 
	 * @see Conventions#attributeNameToPropertyName(String)
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be set on the
	 *            property
	 */
	public static boolean setValueIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		return setValueIfAttributeDefined(builder, element, attributeName,
				Conventions.attributeNameToPropertyName(attributeName));
	}

	/**
	 * Checks the attribute to see if it is defined in the given element.
	 * 
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used as a
	 *            constructor argument
	 */
	public static boolean isAttributeDefined(Element element, String attributeName) {
		String value = element.getAttribute(attributeName);
		return (StringUtils.hasText(value));
	}

	/**
	 * Populates the bean definition constructor argument with the value of that
	 * attribute if it is defined in the given element.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used as a
	 *            constructor argument
	 */
	public static boolean addConstructorArgValueIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		String value = element.getAttribute(attributeName);
		if (StringUtils.hasText(value)) {
			builder.addConstructorArgValue(new TypedStringValue(value));
			return true;
		}
		return false;
	}

	/**
	 * Populates the bean definition constructor argument with the boolean value
	 * of that attribute if it is defined in the given element or else uses the
	 * default provided.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used as a
	 *            constructor argument
	 * @param defaultValue
	 *            the default value to use if the attirbute is not set
	 */
	public static void addConstructorArgBooleanValueIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName, boolean defaultValue) {
		String value = element.getAttribute(attributeName);
		if (StringUtils.hasText(value)) {
			builder.addConstructorArgValue(new TypedStringValue(value));
		} else {
			builder.addConstructorArgValue(defaultValue);
		}
	}

	/**
	 * Populates the bean definition constructor argument with a reference to a
	 * bean with id equal to the attribute if it is defined in the given
	 * element.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used to set the
	 *            reference
	 */
	public static boolean addConstructorArgRefIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		String value = element.getAttribute(attributeName);
		if (StringUtils.hasText(value)) {
			builder.addConstructorArgReference(value);
			return true;
		}
		return false;
	}

	/**
	 * Populates the bean definition constructor argument with a reference to a
	 * bean with parent id equal to the attribute if it is defined in the given
	 * element.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used to set the
	 *            reference
	 */
	public static boolean addConstructorArgParentRefIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		String value = element.getAttribute(attributeName);
		if (StringUtils.hasText(value)) {
			BeanDefinitionBuilder child = BeanDefinitionBuilder.genericBeanDefinition();
			child.setParentName(value);
			builder.addConstructorArgValue(child.getBeanDefinition());
			return true;
		}
		return false;
	}

	/**
	 * Populates the specified bean definition property with the reference to a
	 * bean. The bean reference is identified by the value from the attribute
	 * whose name is provided if that attribute is defined in the given element.
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used as a bean
	 *            reference to populate the property
	 * @param propertyName
	 *            the name of the property to be populated
	 * @return true if the attribute is present and has text
	 */
	public static boolean setReferenceIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName, String propertyName) {
		String attributeValue = element.getAttribute(attributeName);
		if (StringUtils.hasText(attributeValue)) {
			builder.addPropertyReference(propertyName, attributeValue);
			return true;
		}
		return false;
	}

	/**
	 * Populates the bean definition property corresponding to the specified
	 * attributeName with the reference to a bean identified by the value of
	 * that attribute if the attribute is defined in the given element.
	 * 
	 * <p>
	 * The property name will be the camel-case equivalent of the lower case
	 * hyphen separated attribute (e.g. the "foo-bar" attribute would match the
	 * "fooBar" property).
	 * 
	 * @see Conventions#attributeNameToPropertyName(String)
	 * 
	 * @param builder
	 *            the bean definition builder to be configured
	 * @param element
	 *            the XML element where the attribute should be defined
	 * @param attributeName
	 *            the name of the attribute whose value will be used as a bean
	 *            reference to populate the property
	 * 
	 * @see Conventions#attributeNameToPropertyName(String)
	 */
	public static boolean setReferenceIfAttributeDefined(BeanDefinitionBuilder builder, Element element,
			String attributeName) {
		return setReferenceIfAttributeDefined(builder, element, attributeName,
				Conventions.attributeNameToPropertyName(attributeName));
	}

	/**
	 * Provides a user friendly description of an element based on its node name
	 * and, if available, its "id" attribute value. This is useful for creating
	 * error messages from within bean definition parsers.
	 */
	public static String createElementDescription(Element element) {
		String elementId = "'" + element.getNodeName() + "'";
		String id = element.getAttribute("id");
		if (StringUtils.hasText(id)) {
			elementId += " with id='" + id + "'";
		}
		return elementId;
	}

	public static BeanComponentDefinition parseInnerBeanDefinition(Element element, ParserContext parserContext) {
		// parses out inner bean definition for concrete implementation if
		// defined
		List<Element> childElements = DomUtils.getChildElementsByTagName(element, "bean");
		BeanComponentDefinition innerComponentDefinition = null;
		if (childElements != null && childElements.size() == 1) {
			Element beanElement = childElements.get(0);
			BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
			BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(beanElement);
			bdHolder = delegate.decorateBeanDefinitionIfRequired(beanElement, bdHolder);
			BeanDefinition inDef = bdHolder.getBeanDefinition();
			String beanName = BeanDefinitionReaderUtils.generateBeanName(inDef, parserContext.getRegistry());
			innerComponentDefinition = new BeanComponentDefinition(inDef, beanName);
			parserContext.registerBeanComponent(innerComponentDefinition);
		}

		String ref = element.getAttribute(REF_ATTRIBUTE);
		Assert.isTrue(!(StringUtils.hasText(ref) && innerComponentDefinition != null),
				"Ambiguous definition. Inner bean "
						+ (innerComponentDefinition == null ? innerComponentDefinition : innerComponentDefinition
								.getBeanDefinition().getBeanClassName()) + " declaration and \"ref\" " + ref
						+ " are not allowed together.");
		return innerComponentDefinition;
	}

	public static Object getTarget(Object bean) {
		if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
			try {
				return getTarget(((Advised) bean).getTargetSource().getTarget());
			} catch (Exception ex) {
				throw new RuntimeException("Get target error", ex);
			}
		}

		return bean;
	}
}
