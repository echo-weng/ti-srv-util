package me.andpay.ti.spring;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

/**
 * 对象复制器类。
 * 
 * @author sea.bao
 */
public class BeanCopier {
	/**
	 * Copy the property values of the given source bean into the target bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be
	 * derived from each other, as long as the properties match. Any bean
	 * properties that the source bean exposes but the target bean does not will
	 * silently be ignored.
	 * <p>
	 * This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @throws BeansException
	 *             if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		copyProperties(source, target, null, null, false);
	}

	public static void copyPropertiesIfNull(Object source, Object target) throws BeansException {
		copyProperties(source, target, null, null, true);
	}

	/**
	 * Copy the property values of the given source bean into the given target
	 * bean, only setting properties defined in the given "editable" class (or
	 * interface).
	 * <p>
	 * Note: The source and target classes do not have to match or even be
	 * derived from each other, as long as the properties match. Any bean
	 * properties that the source bean exposes but the target bean does not will
	 * silently be ignored.
	 * <p>
	 * This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param editable
	 *            the class (or interface) to restrict property setting to
	 * @throws BeansException
	 *             if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
		copyProperties(source, target, editable, null, false);
	}

	/**
	 * Copy the property values of the given source bean into the given target
	 * bean, ignoring the given "ignoreProperties".
	 * <p>
	 * Note: The source and target classes do not have to match or even be
	 * derived from each other, as long as the properties match. Any bean
	 * properties that the source bean exposes but the target bean does not will
	 * silently be ignored.
	 * <p>
	 * This is just a convenience method. For more complex transfer needs,
	 * consider using a full BeanWrapper.
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param ignoreProperties
	 *            array of property names to ignore
	 * @throws BeansException
	 *             if the copying failed
	 * @see BeanWrapper
	 */
	public static void copyProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
		copyProperties(source, target, null, ignoreProperties, false);
	}

	public static void copyPropertiesIfNull(Object source, Object target, String[] ignoreProperties)
			throws BeansException {
		copyProperties(source, target, null, ignoreProperties, true);
	}

	/**
	 * Copy the property values of the given source bean into the given target
	 * bean.
	 * <p>
	 * Note: The source and target classes do not have to match or even be
	 * derived from each other, as long as the properties match. Any bean
	 * properties that the source bean exposes but the target bean does not will
	 * silently be ignored.
	 * 
	 * @param source
	 *            the source bean
	 * @param target
	 *            the target bean
	 * @param editable
	 *            the class (or interface) to restrict property setting to
	 * @param ignoreProperties
	 *            array of property names to ignore
	 * @throws BeansException
	 *             if the copying failed
	 * @see BeanWrapper
	 */
	private static void copyProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties,
			boolean ifNull) throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName()
						+ "] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null
					&& (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}

						Object value = readMethod.invoke(source);
						if (ifNull && targetPd.getPropertyType().isPrimitive() == false) {
							Object targetValue = targetPd.getReadMethod().invoke(target);
							if (targetValue != null) {
								continue;
							}
						}

						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}

						writeMethod.invoke(target, value);
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}
}
