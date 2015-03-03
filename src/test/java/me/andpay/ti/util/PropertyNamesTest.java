package me.andpay.ti.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 属性名称测试类。
 * 
 * @author sea.bao
 */
public class PropertyNamesTest {

	@Test
	public void test() {
		assertEquals("Url", PropertyNames.capitalize("url"));
		assertEquals("", PropertyNames.capitalize(""));
		
		assertEquals("url", PropertyNames.unixStyleToJavaStyle("url"));
		assertEquals("syncTimeout", PropertyNames.unixStyleToJavaStyle("sync-timeout"));
		assertEquals("syncTimeOut", PropertyNames.unixStyleToJavaStyle("sync-time-out"));
		assertEquals("s", PropertyNames.unixStyleToJavaStyle("s"));
		assertEquals("", PropertyNames.unixStyleToJavaStyle(""));
		assertEquals("", PropertyNames.unixStyleToJavaStyle("-"));
		assertEquals("", PropertyNames.unixStyleToJavaStyle("--"));
		assertEquals("syncTimeOut", PropertyNames.unixStyleToJavaStyle("syncTimeOut"));
	}

}
