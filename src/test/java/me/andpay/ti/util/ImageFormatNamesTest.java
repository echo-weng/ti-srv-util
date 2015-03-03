package me.andpay.ti.util;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 图片格式名称类测试类
 * 
 * @author alex
 */
public class ImageFormatNamesTest {

	@Test
	public void testIsSupportedFormat() {
		Assert.assertTrue(ImageFormatNames.isSupportedFormat(ImageFormatNames.BMP));
		Assert.assertTrue(ImageFormatNames.isSupportedFormat("BmP"));
		Assert.assertFalse(ImageFormatNames.isSupportedFormat("tiff"));
		Assert.assertFalse(ImageFormatNames.isSupportedFormat(null));
	}
}
