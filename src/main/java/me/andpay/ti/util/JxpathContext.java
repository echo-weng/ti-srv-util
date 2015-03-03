package me.andpay.ti.util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.jxpath.JXPathContext;

/**
 * 基于Jxpath的上下文类。
 * 
 * @author sea.bao
 */
public class JxpathContext implements XpathContext {
	/**
	 * 原始对象
	 */
	private Object rawObject;

	/**
	 * xpath上下文
	 */
	private JXPathContext xpathCtx;

	public JxpathContext(Object obj) {
		this.rawObject = obj;
		this.xpathCtx = JXPathContext.newContext(obj);
	}

	public static JxpathContext newInstance(Object obj) {
		JxpathContext ctx = new JxpathContext(obj);
		return ctx;
	}

	public Object getValue(String xpath) {
		return xpathCtx.getValue(xpath);
	}

	public String getString(String xpath) {
		return (String) getValue(xpath);
	}

	public Date getDate(String xpath) {
		return (Date) getValue(xpath);
	}

	public BigDecimal getBigDecimal(String xpath) {
		return (BigDecimal) getValue(xpath);
	}

	public Integer getInteger(String xpath) {
		return (Integer) getValue(xpath);
	}

	public Long getLong(String xpath) {
		return (Long) getValue(xpath);
	}

	public Double getDouble(String xpath) {
		return (Double) getValue(xpath);
	}

	public void setValue(String xpath, Object value) {
		xpathCtx.setValue(xpath, value);
	}

	public Object getRawObject() {
		return rawObject;
	}

}
