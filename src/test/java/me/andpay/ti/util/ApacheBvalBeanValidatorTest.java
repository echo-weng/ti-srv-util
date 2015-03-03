package me.andpay.ti.util;

import static org.junit.Assert.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.junit.Test;

/**
 * ApacheBvalBean校验器测试类。
 * 
 * @author sea.bao
 */
public class ApacheBvalBeanValidatorTest {
	public static class Bean {
		@NotNull
		private String value1;
		
		@Size(max=1)
		private String value2;

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}
		
	}
	
	@Test
	public void test() {
		Bean bean = new Bean();
		try {
			ApacheBvalBeanValidator.validateBean(bean);
			fail("Not to throw exception.");
		} catch(IllegalArgumentException e) {
			System.out.println("exMsg:" + e.getMessage());
		}
		
		try {
			bean.setValue1("123");
			bean.setValue2("abc");
			ApacheBvalBeanValidator.validateBean(bean);
			fail("Not to throw exception.");
		} catch(IllegalArgumentException e) {
			System.out.println("exMsg:" + e.getMessage());
		}
		
		bean.setValue2("a");
		ApacheBvalBeanValidator.validateBean(bean);
	}

}
