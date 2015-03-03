package me.andpay.ti.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * HEX工具测试类
 * 
 * @author alex
 */
public class HexUtilTest {
	/**
	 * 测试decodeHex方法
	 */
	@Test
	public void testDecodeHex() {
		final String hex = "123456789ABCDEF";
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde,
				(byte) 0xf0 }, HexUtil.decodeHex(hex));
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde,
				(byte) 0xff }, HexUtil.decodeHex(hex, 'f'));
		Assert.assertNull(HexUtil.decodeHex(null));
		Assert.assertNull(HexUtil.decodeHex(null, 'f'));
		Assert.assertEquals(0, HexUtil.decodeHex("").length);

		try {
			HexUtil.decodeHex("IHK@#$@"); // hex not in [0-9a-fA-F]
			Assert.fail("Expect exception");
		} catch (Exception ex) {
		}

		try {
			HexUtil.decodeHex(hex, 'i'); // padChar not in [0-9a-fA-F]
			Assert.fail("Expect exception");
		} catch (Exception ex) {
		}
	}

	/**
	 * 测试encodeHex方法
	 */
	@Test
	public void testEncodeHex() {
		final byte[] bytes = new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde, (byte) 0xf0 };
		Assert.assertEquals("123456789ABCDEF0", HexUtil.encodeHex(bytes));
		Assert.assertEquals("123456789abcdef0", HexUtil.encodeHex(bytes, true));
		Assert.assertNull(HexUtil.encodeHex(null));
		Assert.assertNull(HexUtil.encodeHex(null, true));
		Assert.assertEquals("", HexUtil.encodeHex(new byte[0]));
	}
}
