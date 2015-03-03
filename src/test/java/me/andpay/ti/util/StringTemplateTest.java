package me.andpay.ti.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class StringTemplateTest {
	@Test
	public void test() throws Exception {
		StringTemplate template = new StringTemplate("${body}");
		template.replace("${body}", "abc");
		assertEquals("abc", template.getString());
	}

}
