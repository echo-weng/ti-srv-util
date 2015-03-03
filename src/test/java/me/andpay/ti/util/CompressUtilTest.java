package me.andpay.ti.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * 压缩工具测试类
 * 
 * @author alex
 */
public class CompressUtilTest {

	@Test
	public void test() {
		// string compress
		String str = "测试字符串压缩の用例,1234567890";
		String comStr = CompressUtil.compress(str);
		Assert.assertTrue(str.length() < comStr.length()); // 压缩后变大

		String uncomStr = CompressUtil.uncompress(comStr);
		Assert.assertEquals(str, uncomStr);

		StringBuilder largeStr = new StringBuilder();
		int i = 1000;
		while (i-- >= 0) {
			largeStr.append(str);
		}

		// string uncompress
		String comLargeStr = CompressUtil.compress(largeStr.toString());
		Assert.assertTrue(largeStr.length() > comLargeStr.length());

		String uncomLargeStr = CompressUtil.uncompress(comLargeStr);
		Assert.assertEquals(largeStr.toString(), uncomLargeStr);

		// bytes compress
		byte[] bytes = str.getBytes();
		byte[] comBytes = CompressUtil.compress(bytes);
		Assert.assertTrue(bytes.length < comBytes.length); // 压缩后变大

		byte[] uncomBytes = CompressUtil.uncompress(comBytes);
		Assert.assertArrayEquals(uncomBytes, bytes);

		byte[] largeBytes = largeStr.toString().getBytes();
		byte[] comLargeBytes = CompressUtil.compress(largeBytes);
		Assert.assertTrue(largeBytes.length > comLargeBytes.length);

		byte[] uncomLargeBytes = CompressUtil.uncompress(comLargeBytes);
		Assert.assertArrayEquals(uncomLargeBytes, largeBytes);
	}
}
