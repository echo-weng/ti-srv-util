package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.security.SecureRandom;

import org.junit.Test;

/**
 * 字节转换器测试类。
 * 
 * @author sea.bao
 */
public class BytesConvertorTest {

	@Test
	public void test() {
		SecureRandom rnd = new SecureRandom();
		long d;

		do {
			d = rnd.nextLong();
		} while (d < 0);
		
		String str = StringUtil.formatNumber(12, d);
		if ( str.length() > 12 ) {
			str = str.substring(0, 12);
		}
		
		d = Long.valueOf(str, 10);

		System.out.printf("%d\n", d);
		byte[] data = BytesConvertor.convertToBytes(d, 5);
		for ( int i=0; i < data.length; i++ ) {
			System.out.printf("%02x\n", data[i]);
		}
		
		assertTrue(d == BytesConvertor.convertToLong(data));
	}

}
