package me.andpay.ti.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * JXpath上下文测试类。
 * 
 * @author sea.bao
 */
public class JxpathContextTest {
	public static class BeanA {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	@Test
	public void test() {
		BeanA beanA = new BeanA();
		JxpathContext ctx = JxpathContext.newInstance(beanA);
		ctx.setValue("value", "123");
		assertEquals("123", beanA.getValue());
		assertEquals("123", ctx.getString("value"));
	}

}
