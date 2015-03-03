package me.andpay.ti.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 代码消息测试类。
 * 
 * @author sea.bao
 */
public class CodedMessageTest {

	@Test
	public void test() {
		CodedMessage cmsg = CodedMessage.newCodedMessage("001", "v1", "v2");
		assertEquals("001", cmsg.getCode());
		assertEquals(2, cmsg.getProperties().size());
		assertEquals("v1", cmsg.getProperties().get("a0"));
		assertEquals("v2", cmsg.getProperties().get("a1"));
	}

}
