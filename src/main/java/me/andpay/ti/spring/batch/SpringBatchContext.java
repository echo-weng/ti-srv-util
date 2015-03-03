package me.andpay.ti.spring.batch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringBatch上下文类。
 * 
 * @author sea.bao
 */
public class SpringBatchContext {
	protected static ThreadLocal<Map<String, Object>> map = new ThreadLocal<Map<String, Object>>();
	
	public static void reset() {
		map.remove();
	}
	
	public static Object setValue(String name, Object value) {
		Map<String, Object> m = map.get();
		if ( m == null ) {
			m = new HashMap<String, Object>();
			map.set(m);
		}
		
		return m.put(name, value);
	}
	
	public static Object getValue(String name) {
		Map<String, Object> m = map.get();
		if ( m == null ) {
			return null;
		}
		
		return m.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(String name, Class<T> clazz) {
		return (T )getValue(name);
	}
	
	public static String getString(String name) {
		return getValue(name, String.class);
	}
	
	public static Integer getInteger(String name) {
		return getValue(name, Integer.class);
	}
	
	public static BigDecimal getBigDecimal(String name) {
		return getValue(name, BigDecimal.class);
	}
	
	public static Date getDate(String name) {
		return getValue(name, Date.class);
	}
}
