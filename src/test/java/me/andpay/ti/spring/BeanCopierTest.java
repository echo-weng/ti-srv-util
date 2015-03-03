package me.andpay.ti.spring;

import static org.junit.Assert.*;
import java.math.BigDecimal;

import org.junit.Test;

/**
 * Bean复制器测试类。
 * 
 * @author sea.bao
 */
public class BeanCopierTest {
	public static class BeanA {
		private String value1;
		
		private int value2;
		
		private BigDecimal value3;

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public int getValue2() {
			return value2;
		}

		public void setValue2(int value2) {
			this.value2 = value2;
		}

		public BigDecimal getValue3() {
			return value3;
		}

		public void setValue3(BigDecimal value3) {
			this.value3 = value3;
		}
		
	}
	
	@Test
	public void test() {
		BeanA bean1 = new BeanA();
		bean1.setValue1("123");
		bean1.setValue2(5);
		bean1.setValue3(null);
		
		BeanA bean2 = new BeanA();
		bean2.setValue2(10);
		bean2.setValue3(new BigDecimal("100.00"));
		
		BeanCopier.copyPropertiesIfNull(bean1, bean2);
		
		assertEquals(5, bean2.getValue2());
		assertEquals("123", bean2.getValue1());
		assertTrue(100.00 == bean2.getValue3().doubleValue());
	}

}
