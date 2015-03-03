package me.andpay.ti.util;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 图片质量类测试类
 * 
 * @author alex
 */
public class ImageQualitiesTest {
	@Test
	public void testIsValidQuality() {
		Assert.assertTrue(ImageQualities.isValidQuality(1F));
		Assert.assertTrue(ImageQualities.isValidQuality(0.55F));
		Assert.assertFalse(ImageQualities.isValidQuality(0F));
		Assert.assertFalse(ImageQualities.isValidQuality(1.1F));
		Assert.assertFalse(ImageQualities.isValidQuality(-1F));
	}
}
