package me.andpay.ti.util;

import me.andpay.ti.util.ArrayUtil;

/**
 * 图片格式名称常量类
 * 
 * @author alex
 */
public final class ImageFormatNames {
	/**
	 * JPEG图片
	 */
	public static final String JPG = "jpg";

	/**
	 * PNG图片
	 */
	public static final String PNG = "png";

	/**
	 * GIT图片
	 */
	public static final String GIF = "gif";

	/**
	 * BMP图片
	 */
	public static final String BMP = "bmp";

	/**
	 * 可支持的图片名称
	 */
	private static String[] SUPPORT_FOMRATS = { JPG, GIF, PNG, BMP };

	/**
	 * 是否是可支持的图片格式
	 * 
	 * @param format
	 * @return
	 */
	public static boolean isSupportedFormat(String format) {
		if (format == null) {
			return false;
		}

		return ArrayUtil.contains(SUPPORT_FOMRATS, format.toLowerCase());
	}

	private ImageFormatNames() {
	}
}
