package me.andpay.ti.util;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Xpath上下文接口定义类。
 * 
 * @author sea.bao
 */
public interface XpathContext {
	/**
	 * 获得路径对应的数值
	 * 
	 * @param xpath
	 * @return
	 */
	Object getValue(String xpath);

	/**
	 * 获得路径对应的数值String
	 * 
	 * @param xpath
	 * @return
	 */
	String getString(String xpath);

	/**
	 * 获得路径对应的数值Date
	 * 
	 * @param xpath
	 * @return
	 */
	Date getDate(String xpath);

	/**
	 * 获得路径对应的数值BigDecimal
	 * 
	 * @param xpath
	 * @return
	 */
	BigDecimal getBigDecimal(String xpath);

	/**
	 * 获得路径对应的数值Integer
	 * 
	 * @param xpath
	 * @return
	 */
	Integer getInteger(String xpath);

	/**
	 * 获得路径对应的数值Integer
	 * 
	 * @param xpath
	 * @return
	 */
	Long getLong(String xpath);

	/**
	 * 获得路径对应的数值Double
	 * 
	 * @param xpath
	 * @return
	 */
	Double getDouble(String xpath);

	/**
	 * 设置数值到对应的路径
	 * 
	 * @param xpath
	 * @param value
	 */
	void setValue(String xpath, Object value);
	
	/**
	 * 获得原始对象
	 */
	Object getRawObject();
}
