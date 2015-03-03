package me.andpay.ti.util;

/**
 * 字节转换器类。
 * 
 * @author sea.bao
 */
public class BytesConvertor {
	public static byte[] convertToBytes(long d, int size) {
		byte[] data = new byte[size];
		for (int i = 0; i < size; i++) {
			data[i] = (byte) ((d >> (i * 8)) & 0x0ff);
		}

		return data;
	}

	public static long convertToLong(byte[] data) {
		long d = 0;
		for (int i = 0; i < data.length; i++) {
			d |= ((long )(data[i]&0x0ff)) << (i * 8);
		}

		return d;
	}
}
