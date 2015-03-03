package me.andpay.ti.spring;

import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * 属性工具类。
 * 
 * @author sea.bao
 */
public class PropertyUtil {
	protected static Log logger = LogFactory.getLog(PropertyUtil.class);

	public static void setProperties(List<Object> beans, List<String> propertyNames, List<String> propertyValues) {
		int size = beans.size();
		for (int i = 0; i < size; i++) {
			JXPathContext context = JXPathContext.newContext(beans.get(i));
			try {
				context.setValue(propertyNames.get(i), propertyValues.get(i));
			} catch (RuntimeException e) {
				logger.error("Set property meet error, bean=[" + beans.get(i).getClass().getName()
						+ "], propertyName=[" + propertyNames.get(i) + "].");
				throw e;
			}
		}
	}

	public static void setPropeties(PropertiesHolder holder) {
		setProperties(holder.getPropertyBeans(), holder.getPropertyNames(), holder.getPropertyValues());
	}

	public static void setRefs(ApplicationContext appCtx, List<Object> beans, List<String> propertyNames,
			List<Object> propertyRefs) {
		setRefs(appCtx, beans, propertyNames, propertyRefs, null);
	}
	
	public static void setRefs(ApplicationContext appCtx, List<Object> beans, List<String> propertyNames,
			List<Object> propertyRefs, List<Boolean> refRequireds) {
		for (int i = 0; i < beans.size(); i++) {
			Object bean = beans.get(i);
			String propertyName = propertyNames.get(i);
			Object ref = propertyRefs.get(i);
			boolean required = true;
			if (refRequireds != null) {
				required = refRequireds.get(i);
			}

			JXPathContext context = JXPathContext.newContext(bean);
			if (ref != null) {
				try {
					if (ref instanceof String) {
						ref = appCtx.getBean((String) ref);
					} else {
						ref = appCtx.getBean((Class<?>) ref);
					}
				} catch (NoSuchBeanDefinitionException e) {
					if (required) {
						throw e;
					} else {
						ref = null;
					}
				}
			}

			context.setValue(propertyName, ref);
		}
	}

	public static void setRefs(ApplicationContext appCtx, PropertiesHolder holder) {
		setRefs(appCtx, holder.getRefBeans(), holder.getRefNames(), holder.getRefValues(), holder.getRefRequireds());
	}
}
