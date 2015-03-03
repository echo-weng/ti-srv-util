package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.junit.Test;

/**
 * 基于ApacheBval实现的方法校验器测试类。
 * 
 * @author sea.bao
 */
public class ApacheBvalMethodValidatorTest {
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
	
	public void call(@NotNull String value, @Valid Bean bean) {
	}
	
	@Test
	public void testValidateAllParameters() throws Exception {
		Method method = ApacheBvalMethodValidatorTest.class.getMethod("call", String.class, Bean.class);
		Bean bean = new Bean();
		bean.setValue1(null);
		bean.setValue2("123");
		
		try {
			ApacheBvalMethodValidator.validateAllParameters(ApacheBvalMethodValidatorTest.class, method, null, bean);
			fail("not to throw exception.");
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

}
