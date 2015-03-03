package me.andpay.ti.iso8583;

import java.util.List;

import me.andpay.ti.util.ByteUtil;
import me.andpay.ti.util.Charsets;
import me.andpay.ti.util.HexUtil;
import me.andpay.ti.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * 标准TLV测试类
 * 
 * @author alex
 */
public class TLVListTest {

	@Test
	public void testAGD() {
		TLVList tlvList = new TLVList();

		TLVObject tlvObj = new TLVObject();
		tlvObj.setHexTag("1234");
		tlvObj.setStringValue("测试123abcDEF");

		// calc tlv length
		Assert.assertEquals(2 + 1 + 13, TLVList.calcTLVFullLength(tlvObj));

		// add
		tlvList.add(tlvObj);

		Assert.assertEquals(1, tlvList.size());
		Assert.assertEquals(tlvObj, tlvList.get(tlvObj.getHexTag()));

		tlvList.addBytes(tlvObj.getTag(), tlvObj.getValue());
		Assert.assertEquals(2, tlvList.size());
		Assert.assertEquals(2, tlvList.getAll(tlvObj.getHexTag()).size());
		TLVObject newTlvObj = tlvList.get(tlvObj.getHexTag()); // first
		Assert.assertSame(tlvObj, newTlvObj);
		Assert.assertNotSame(newTlvObj, tlvList.getAll(tlvObj.getHexTag()).get(1)); // idx=2
		Assert.assertArrayEquals(tlvObj.getTag(), newTlvObj.getTag());
		Assert.assertArrayEquals(tlvObj.getValue(), newTlvObj.getValue());
		Assert.assertArrayEquals(tlvObj.getValue(), tlvList.getBytes(tlvObj.getHexTag()));

		tlvList.addString(tlvObj.getHexTag(), tlvObj.getStringValue());
		Assert.assertEquals(3, tlvList.size());
		Assert.assertEquals(3, tlvList.getAll(tlvObj.getHexTag()).size());
		newTlvObj = tlvList.get(tlvObj.getHexTag()); // first
		Assert.assertSame(tlvObj, newTlvObj);
		Assert.assertNotSame(newTlvObj, tlvList.getAll(tlvObj.getHexTag()).get(2)); // idx=3
		Assert.assertArrayEquals(tlvObj.getTag(), newTlvObj.getTag());
		Assert.assertArrayEquals(tlvObj.getValue(), newTlvObj.getValue());
		Assert.assertEquals(tlvObj.getHexValue(), tlvList.getHexString(tlvObj.getHexTag()));

		tlvList.addString(tlvObj.getHexTag(), tlvObj.getStringValue(Charsets.GBK), Charsets.GBK);
		Assert.assertEquals(4, tlvList.size());
		Assert.assertEquals(4, tlvList.getAll(tlvObj.getHexTag()).size());
		newTlvObj = tlvList.get(tlvObj.getHexTag()); // first
		Assert.assertSame(tlvObj, newTlvObj);
		Assert.assertNotSame(newTlvObj, tlvList.getAll(tlvObj.getHexTag()).get(3)); // idx=4
		Assert.assertArrayEquals(tlvObj.getTag(), newTlvObj.getTag());
		Assert.assertArrayEquals(tlvObj.getValue(), newTlvObj.getValue());
		Assert.assertEquals(tlvObj.getStringValue(Charsets.GBK), tlvList.getString(tlvObj.getHexTag(), Charsets.GBK));

		tlvList.addBytes(new byte[4], new byte[4]);
		Assert.assertEquals(5, tlvList.size());

		tlvList.addHexString("0000", "00000000");
		Assert.assertEquals(6, tlvList.size());

		// remove first
		Assert.assertSame(newTlvObj, tlvList.remove(tlvObj.getHexTag()));
		Assert.assertEquals(5, tlvList.size());

		// remove all
		Assert.assertSame(3, tlvList.removeAll(tlvObj.getHexTag()).size());
		Assert.assertEquals(2, tlvList.size());

		// tlv full length
		Assert.assertEquals(4 + 1 + 4 + 2 + 1 + 4, tlvList.getTLVFullLength());

		// add tlv list
		tlvList.addTLVList(tlvList);
		Assert.assertEquals(4, tlvList.size());
		Assert.assertEquals(2 * (4 + 1 + 4 + 2 + 1 + 4), tlvList.getTLVFullLength());

		tlvList.addTLVList(null);
		Assert.assertEquals(4, tlvList.size());
	}

	@Test
	public void testPack() throws Exception {
		final String tag1Hex = "12";
		final byte[] tag1 = HexUtil.decodeHex(tag1Hex);

		final String val1Str = "测试123abcDEF";
		final byte[] val1 = val1Str.getBytes(Charsets.GBK);

		final String tag2Hex = "9F12";
		final byte[] tag2 = HexUtil.decodeHex(tag2Hex);

		final String val2Str = "~!@#$%^&*";
		final byte[] val2 = val2Str.getBytes(Charsets.GBK);

		final String tag3Hex = "9F12";
		final byte[] tag3 = HexUtil.decodeHex(tag3Hex);

		final String val3Str = StringUtil.padding("", '*', 260);
		final byte[] val3 = val3Str.getBytes(Charsets.GBK);

		// pack
		TLVObject tlvObj1 = new TLVObject();
		tlvObj1.setTag(tag1);
		tlvObj1.setValue(val1);

		TLVObject tlvObj2 = new TLVObject();
		tlvObj2.setTag(tag2);
		tlvObj2.setValue(val2);

		TLVObject tlvObj3 = new TLVObject();
		tlvObj3.setTag(tag3);
		tlvObj3.setValue(val3);

		TLVList tlvList4Pack = new TLVList();
		tlvList4Pack.add(tlvObj1);
		tlvList4Pack.add(tlvObj2);
		tlvList4Pack.add(tlvObj3);

		byte[] tlvData = tlvList4Pack.pack();

		// T + L + V
		Assert.assertEquals(1 + 1 + val1.length + 2 + 1 + val2.length + 2 + 3 + val3.length, tlvData.length);

		// TLV 1
		Assert.assertArrayEquals(tag1, ByteUtil.getBytes(tlvData, 0, 1));
		Assert.assertArrayEquals(ByteUtil.parseBytes(val1.length, 1), ByteUtil.getBytes(tlvData, 1, 1));
		Assert.assertArrayEquals(val1, ByteUtil.getBytes(tlvData, 2, val1.length));

		// TLV 2
		Assert.assertArrayEquals(tag2, ByteUtil.getBytes(tlvData, 2 + val1.length, 2));
		Assert.assertArrayEquals(ByteUtil.parseBytes(val2.length, 1), ByteUtil.getBytes(tlvData, 4 + val1.length, 1));
		Assert.assertArrayEquals(val2, ByteUtil.getBytes(tlvData, 5 + val1.length, val2.length));

		// TLV 3
		Assert.assertArrayEquals(tag3, ByteUtil.getBytes(tlvData, 5 + val1.length + val2.length, 2));
		Assert.assertArrayEquals(ByteUtil.parseBytes(val3.length, 2),
				ByteUtil.getBytes(tlvData, 7 + 1 + val1.length + val2.length, 2));
		Assert.assertArrayEquals(val3, ByteUtil.getBytes(tlvData, 10 + val1.length + val2.length, val3.length));

		// unpack 1
		TLVList tlvList4Unpack = new TLVList();
		tlvList4Unpack.unpack(tlvData);
		Assert.assertEquals(3, tlvList4Unpack.size());

		TLVObject unpackTlvObj1 = tlvList4Unpack.get(tlvObj1.getHexTag());
		Assert.assertArrayEquals(tlvObj1.getTag(), unpackTlvObj1.getTag());
		Assert.assertArrayEquals(tlvObj1.getValue(), unpackTlvObj1.getValue());

		List<TLVObject> unpackTlvObj23 = tlvList4Unpack.getAll(tlvObj2.getHexTag());
		Assert.assertEquals(2, unpackTlvObj23.size());

		// TLV 2
		Assert.assertArrayEquals(tlvObj2.getTag(), unpackTlvObj23.get(0).getTag());
		Assert.assertArrayEquals(tlvObj2.getValue(), unpackTlvObj23.get(0).getValue());

		// TLV 3
		Assert.assertArrayEquals(tlvObj3.getTag(), unpackTlvObj23.get(1).getTag());
		Assert.assertArrayEquals(tlvObj3.getValue(), unpackTlvObj23.get(1).getValue());

		// unpack 2
		TLVList tvlList3 = new TLVList();
		tvlList3.unpack(null);
		Assert.assertEquals(0, tvlList3.size());

		tvlList3.unpack(new byte[0]);
		Assert.assertEquals(0, tvlList3.size());

		// compare with jpos class
		org.jpos.tlv.TLVList jposTlvList = new org.jpos.tlv.TLVList();
		jposTlvList.append((int) ByteUtil.toLong(tlvObj1.getTag()), tlvObj1.getValue());
		jposTlvList.append((int) ByteUtil.toLong(tlvObj2.getTag()), tlvObj2.getValue());
		jposTlvList.append((int) ByteUtil.toLong(tlvObj3.getTag()), tlvObj3.getValue());

		byte[] jposTlvData = jposTlvList.pack();
		Assert.assertArrayEquals(jposTlvData, tlvData);
	}
}
