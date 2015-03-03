package me.andpay.ti.spring.batch;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SpringBatch上下文支持类。
 * 
 * @author sea.bao
 */
public abstract class SpringBatchContextSupport {
	protected Object setValue(String name, Object value) {
		return SpringBatchContext.setValue(name, value);
	}
	
	protected Object getValue(String name) {
		return SpringBatchContext.getValue(name);
	}
	
	protected <T> T getValue(String name, Class<T> clazz) {
		return SpringBatchContext.getValue(name, clazz);
	}
	
	protected String getString(String name) {
		return SpringBatchContext.getString(name);
	}
	
	protected Integer getInteger(String name) {
		return SpringBatchContext.getInteger(name);
	}
	
	protected BigDecimal getBigDecimal(String name) {
		return SpringBatchContext.getBigDecimal(name);
	}
	
	protected Date getDate(String name) {
		return SpringBatchContext.getDate(name);
	}
}
