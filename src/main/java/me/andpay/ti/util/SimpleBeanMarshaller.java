package me.andpay.ti.util;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.jxpath.JXPathContext;
import org.springframework.beans.BeanUtils;

/**
 * 简单Bean映射器类，只支持一级、简单数值类型属性的映射
 * 
 * @author sea.bao
 */
public class SimpleBeanMarshaller {
	static String marshallPropertyValue(String propName, Object value) {
		if (value == null) {
			return "";
		}

		if (value instanceof java.util.Date) {
			return Long.toString(((java.util.Date) value).getTime(), 10);
		}

		String v = value.toString();
		if (v.indexOf(",") >= 0 || v.indexOf("=") >= 0) {
			// 属性值不能包括','|'='字符
			throw new RuntimeException("The property=[" + propName + "] has contain illegal character.");
		}

		return v;
	}

	static Object unmarshallPropertyValue(String str, Class<?> propertyType) {
		if (str.equals("")) {
			return null;
		}

		if (propertyType.equals(java.util.Date.class)) {
			java.util.Date date = new java.util.Date();
			date.setTime(Long.valueOf(str, 10));
			return date;
		}

		return str;
	}

	public static String marshall(Object bean, Map<String, String> propMap) {
		if (propMap != null) {
			// 检查属性映射是否可以反转映射
			revertPropMap(propMap);
		}

		StringBuffer sb = new StringBuffer();
		boolean first = true;
		JXPathContext ctx = JXPathContext.newContext(bean);
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(bean.getClass());
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() == null || pd.getReadMethod() == null) {
				continue;
			}

			if (first) {
				first = false;
			} else {
				sb.append(",");
			}

			Object value = ctx.getValue(pd.getName());
			String propName = null;
			if (propMap != null) {
				propName = propMap.get(pd.getName());
			}

			if (propName == null) {
				propName = pd.getName();
			}

			sb.append(propName);
			sb.append("=");
			sb.append(marshallPropertyValue(pd.getName(), value));
		}

		return sb.toString();
	}

	static Map<String, String> revertPropMap(Map<String, String> propMap) {
		Map<String, String> revPropMap = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : propMap.entrySet()) {
			if (revPropMap.put(entry.getValue(), entry.getKey()) != null) {
				throw new RuntimeException("The propMap isn't one-to-one map.");
			}
		}

		return revPropMap;
	}

	public static void unmarshall(String str, Object bean, Map<String, String> propMap) {
		propMap = revertPropMap(propMap);
		if (str == null) {
			return;
		}

		JXPathContext ctx = JXPathContext.newContext(bean);
		ctx.setLenient(true);

		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			int idx = line.indexOf("=");
			String propName = line.substring(0, idx);
			if (propMap != null) {
				String p = propMap.get(propName);
				if (p != null) {
					propName = p;
				}
			}

			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(bean.getClass(), propName);
			if (pd == null) {
				// 忽略不存在的属性
				continue;
			}

			String propValue = line.substring(idx + 1);
			Object v = unmarshallPropertyValue(propValue, pd.getPropertyType());
			ctx.setValue(propName, v);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshall(String str, Class<T> clazz, Map<String, String> propMap) {
		Object bean;

		try {
			bean = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		unmarshall(str, bean, propMap);
		return (T) bean;
	}
}
