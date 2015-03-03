package me.andpay.ti.util;

/**
 * Spel数值上下文类。
 * 
 * @author sea.bao
 */
public interface SpelValueContext {
	/**
	 * 获得属性值
	 * @param name
	 * @return
	 */
	Object getValue(Object name);
}
