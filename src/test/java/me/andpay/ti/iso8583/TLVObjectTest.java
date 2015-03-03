package me.andpay.ti.iso8583;

import me.andpay.ti.util.Charsets;

import org.junit.Assert;
import org.junit.Test;

/**
 * TLV对象测试类
 * 
 * @author alex
 */
public class TLVObjectTest {

	@Test
	public void test() throws Exception {
		final String tagHex = "1234";
		final byte[] tag = { 0x12, 0x34 };

		final String value = "测试123abcDEF";
		final byte[] valueGBK = value.getBytes(Charsets.GBK);

		// 1
		TLVObject tlvObj = new TLVObject();
		tlvObj.setTag(tag);
		tlvObj.setValue(valueGBK);

		Assert.assertArrayEquals(tag, tlvObj.getTag());
		Assert.assertArrayEquals(valueGBK, tlvObj.getValue());

		Assert.assertEquals(tagHex, tlvObj.getHexTag());
		Assert.assertEquals(value, tlvObj.getStringValue());
		Assert.assertEquals(value, tlvObj.getStringValue(Charsets.GBK));
		Assert.assertFalse(value.equals(tlvObj.getStringValue(Charsets.UTF_8)));

		// 2
		tlvObj.setHexTag(tagHex);
		tlvObj.setStringValue(value, Charsets.GBK);

		Assert.assertArrayEquals(tag, tlvObj.getTag());
		Assert.assertArrayEquals(valueGBK, tlvObj.getValue());

		Assert.assertEquals(tagHex, tlvObj.getHexTag());
		Assert.assertEquals(value, tlvObj.getStringValue());
		Assert.assertEquals(value, tlvObj.getStringValue(Charsets.GBK));
		Assert.assertFalse(value.equals(tlvObj.getStringValue(Charsets.UTF_8)));

		// 3
		tlvObj.setHexTag(null);
		tlvObj.setStringValue(null);
		tlvObj.setStringValue(null, Charsets.GBK);

		Assert.assertNull(tlvObj.getTag());
		Assert.assertNull(tlvObj.getHexTag());
		Assert.assertNull(tlvObj.getValue());
		Assert.assertNull(tlvObj.getStringValue());
		Assert.assertNull(tlvObj.getStringValue(Charsets.GBK));
	}
}
