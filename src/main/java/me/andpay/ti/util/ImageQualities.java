package me.andpay.ti.util;

/**
 * 图片质量常量类
 * 
 * @author alex
 */
public final class ImageQualities {
	/**
	 * 图片质量 - 原画
	 */
	public static final float RAW = 1F;

	/**
	 * 图片质量 - 高质量
	 */
	public static final float HIGH = 0.75F;

	/**
	 * 图片质量 - 中等质量
	 */
	public static final float MEDIUM = 0.50F;

	/**
	 * 图片质量 - 低质量
	 */
	public static final float LOW = 0.25F;

	/**
	 * 是否是合法的图片质量
	 * 
	 * @param quality
	 * @return
	 */
	public static boolean isValidQuality(float quality) {
		return (quality > 0 && quality <= 1F);
	}

	private ImageQualities() {
	}
}
