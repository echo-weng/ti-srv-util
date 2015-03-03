package me.andpay.ti.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * 压缩工具类
 * 
 * @author alex
 */
public final class CompressUtil {
	/**
	 * 默认字符集
	 */
	private static final Charset CHARSET = Charset.forName(Charsets.UTF_8);

	/**
	 * 压缩字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String compress(String str) {
		if (StringUtil.isEmpty(str)) {
			return str;
		}

		byte[] data = str.getBytes(CHARSET);
		byte[] comData = compress(data);
		return Base64.encodeBase64String(comData);
	}

	/**
	 * 加压缩字符串
	 * 
	 * @param comStr
	 * @return
	 */
	public static String uncompress(String comStr) {
		if (StringUtil.isEmpty(comStr)) {
			return comStr;
		}

		byte[] comData = Base64.decodeBase64(comStr);
		byte[] data = uncompress(comData);
		return new String(data, CHARSET);
	}

	/**
	 * 压缩字节数组
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] compress(byte[] data) {
		if (ByteUtil.isEmpty(data)) {
			return data;
		}

		ByteArrayOutputStream out = null;
		GZIPOutputStream gzip = null;
		try {
			out = new ByteArrayOutputStream();
			gzip = new GZIPOutputStream(out);
			gzip.write(data);
		} catch (Exception ex) {
			throw new RuntimeException("Compress byte array error", ex);

		} finally {
			IOUtil.closeQuietly(gzip);
		}

		return out.toByteArray();
	}

	/**
	 * 解压缩字节数组
	 * 
	 * @param comData
	 * @return
	 */
	public static byte[] uncompress(byte[] comData) {
		if (ByteUtil.isEmpty(comData)) {
			return comData;
		}

		ByteArrayOutputStream out = null;
		GZIPInputStream gzip = null;
		try {
			gzip = new GZIPInputStream(new ByteArrayInputStream(comData));
			out = new ByteArrayOutputStream();

			IOUtil.copy(gzip, out);
		} catch (Exception ex) {
			throw new RuntimeException("Uncompress byte array error", ex);

		} finally {
			IOUtil.closeQuietly(out);
			IOUtil.closeQuietly(gzip);
		}

		return out.toByteArray();
	}

	private CompressUtil() {
	}
}
