package me.andpay.ti.iso8583;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.andpay.ti.util.ByteUtil;
import me.andpay.ti.util.SimpleByteBuffer;

/**
 * 标准TLV数据列表
 * 
 * @author alex
 */
public class TLVList {
	/**
	 * TLV数据集合
	 */
	private List<TLVObject> tlvObjs = new ArrayList<TLVObject>();

	/**
	 * 计算TLV数据总长度 length(T + L + V)
	 * 
	 * @param tlvObj
	 * @return
	 */
	public static int calcTLVFullLength(TLVObject tlvObj) {
		return ByteUtil.length(tlvObj.getTag(), getTLVLength(tlvObj), tlvObj.getValue());
	}

	/**
	 * 获取TLVObject内容对应的长度<br />
	 * 
	 * 当L字段最左边字节的最左bit位(即bit8)为0,表示该L字段占一个字节，它的后续7个bit位(即bit7~bit1)表示子域取值的长度，
	 * 采用二进制数表示子域取值长度的十进制数。<br />
	 * 当L字段最左边字节的最左bit位(即bit8)为1,表示该L字段不止占一个字节，那么它到底占几个字节由该最左字节的后续7个bit位(即bit7~
	 * bit1)的十进制取值表示。
	 * 
	 * @param tlvObj
	 * @return
	 */
	private static byte[] getTLVLength(TLVObject tlvObj) {
		int length = ByteUtil.length(tlvObj.getValue());
		byte[] lenInBytes = ByteUtil.parseBytes(length);

		if (lenInBytes.length == 1 && length <= 127) {
			// 内容长度在0-127，长度位为1字节
			return lenInBytes;
		}

		// 内容长度大于127，1字节表示长度所占字节数，N字节表示实际长度
		return ByteUtil.concat(new byte[] { (byte) (0x80 | lenInBytes.length) }, lenInBytes);
	}

	/**
	 * 根据标签HEX获取第一个匹配的TLV对象
	 * 
	 * @param tagHex
	 * @return
	 */
	public TLVObject get(String tagHex) {
		for (TLVObject tlvObj : tlvObjs) {
			if (tlvObj.getHexTag().equals(tagHex)) {
				return tlvObj;
			}
		}

		return null;
	}

	/**
	 * 根据标签HEX获取所有符合的TLV对象集
	 * 
	 * @param tagHex
	 * @return
	 */
	public List<TLVObject> getAll(String tagHex) {
		List<TLVObject> matched = new ArrayList<TLVObject>();
		for (TLVObject tlvObj : tlvObjs) {
			if (tlvObj.getHexTag().equals(tagHex)) {
				matched.add(tlvObj);
			}
		}

		return matched;
	}

	/**
	 * 获得第一个匹配的TLV对象的HEX字符串值
	 * 
	 * @param tagHex
	 * @return
	 */
	public String getHexString(String tagHex) {
		TLVObject tlvObj = get(tagHex);
		return (tlvObj != null ? tlvObj.getHexValue() : null);
	}

	/**
	 * 获得所有匹配的HEX字符串值集合
	 * 
	 * @param tagHex
	 * @return
	 */
	public List<String> getAllHexString(String tagHex) {
		List<String> matched = new ArrayList<String>();
		for (TLVObject tlvObj : tlvObjs) {
			if (tlvObj.getHexTag().equals(tagHex)) {
				matched.add(tlvObj.getHexValue());
			}
		}

		return matched;
	}

	/**
	 * 获得第一个匹配的TLV对象的字符串值，使用指定字符集
	 * 
	 * @param tagHex
	 * @param charset
	 * @return
	 */
	public String getString(String tagHex, String charset) {
		TLVObject tlvObj = get(tagHex);
		return (tlvObj != null ? tlvObj.getStringValue(charset) : null);
	}

	/**
	 * 获得所有匹配的字符串值集合，使用指定字符集
	 * 
	 * @param tagHex
	 * @param charset
	 * @return
	 */
	public List<String> getAllString(String tagHex, String charset) {
		List<String> matched = new ArrayList<String>();
		for (TLVObject tlvObj : tlvObjs) {
			if (tlvObj.getHexTag().equals(tagHex)) {
				matched.add(tlvObj.getStringValue(charset));
			}
		}

		return matched;
	}

	/**
	 * 获得第一个匹配的TLV对象的字节数组值，使用指定字符集
	 * 
	 * @param tagHex
	 * @return
	 */
	public byte[] getBytes(String tagHex) {
		TLVObject tlvObj = get(tagHex);
		return (tlvObj != null ? tlvObj.getValue() : null);
	}

	/**
	 * 获得所有匹配的字节数组值集合
	 * 
	 * @param tagHex
	 * @return
	 */
	public List<byte[]> getAllBytes(String tagHex) {
		List<byte[]> matched = new ArrayList<byte[]>();
		for (TLVObject tlvObj : tlvObjs) {
			if (tlvObj.getHexTag().equals(tagHex)) {
				matched.add(tlvObj.getValue());
			}
		}

		return matched;
	}

	/**
	 * 添加TLV对象
	 * 
	 * @param tlvObj
	 */
	public void add(TLVObject tlvObj) {
		if (tlvObj == null) {
			throw new IllegalArgumentException("TLVObj cannot be null");
		}

		tlvObjs.add(tlvObj);
	}

	/**
	 * 根据tagHex、value构造TLVObject对象并添加
	 * 
	 * @param tagHex
	 * @param value
	 */
	public void addString(String tagHex, String value) {
		TLVObject tlvObj = new TLVObject();
		tlvObj.setHexTag(tagHex);
		tlvObj.setStringValue(value);

		add(tlvObj);
	}

	/**
	 * 根据tagHex、value和charset构造TLVObject对象并添加
	 * 
	 * @param tagHex
	 * @param value
	 * @param charset
	 */
	public void addString(String tagHex, String value, String charset) {
		TLVObject tlvObj = new TLVObject();
		tlvObj.setHexTag(tagHex);
		tlvObj.setStringValue(value, charset);

		add(tlvObj);
	}

	/**
	 * 根据tag和value构造TLVObject对象并添加
	 * 
	 * @param tag
	 * @param value
	 */
	public void addBytes(byte[] tag, byte[] value) {
		TLVObject tlvObj = new TLVObject();
		tlvObj.setTag(tag);
		tlvObj.setValue(value);

		add(tlvObj);
	}

	/**
	 * 根据tagHex和valueHex构造TLVObject对象并添加
	 * 
	 * @param tagHex
	 * @param valueHex
	 */
	public void addHexString(String tagHex, String valueHex) {
		TLVObject tlvObj = new TLVObject();
		tlvObj.setHexTag(tagHex);
		tlvObj.setHexValue(valueHex);

		add(tlvObj);
	}

	/**
	 * 添加TLVList中所有对象集合
	 * 
	 * @param tlvList
	 */
	public void addTLVList(TLVList tlvList) {
		if (tlvList != null) {
			this.tlvObjs.addAll(tlvList.tlvObjs);
		}
	}

	/**
	 * 获取所有TLV对象集合
	 * 
	 * @return
	 */
	public List<TLVObject> getTLVObjects() {
		return new ArrayList<TLVObject>(tlvObjs);
	}

	/**
	 * 移除标签对应的第一个匹配的TLV对象
	 * 
	 * @param tagHex
	 * @return 被移除的TLV对象
	 */
	public TLVObject remove(String tagHex) {
		for (Iterator<TLVObject> i = tlvObjs.iterator(); i.hasNext();) {
			TLVObject tlvObj = i.next();
			if (tlvObj.getHexTag().equals(tagHex)) {
				i.remove();
				return tlvObj;
			}
		}

		return null;
	}

	/**
	 * 移除标签对应的所有匹配的TLV对象集合
	 * 
	 * @param tagHex
	 * @return 被移除的TLV对象集合
	 */
	public List<TLVObject> removeAll(String tagHex) {
		List<TLVObject> removed = new ArrayList<TLVObject>();
		for (Iterator<TLVObject> i = tlvObjs.iterator(); i.hasNext();) {
			TLVObject tlvObj = i.next();
			if (tlvObj.getHexTag().equals(tagHex)) {
				i.remove();
				removed.add(tlvObj);
			}
		}

		return removed;
	}

	/**
	 * TLV字段数量
	 * 
	 * @return
	 */
	public int size() {
		return tlvObjs.size();
	}

	/**
	 * 获得TLV数据总长度
	 * 
	 * @return
	 */
	public int getTLVFullLength() {
		int len = 0;
		for (TLVObject tlvObj : tlvObjs) {
			len += calcTLVFullLength(tlvObj);
		}

		return len;
	}

	/**
	 * 根据TLV字段值，组装TLV域数据
	 * 
	 * @return
	 */
	public byte[] pack() {
		SimpleByteBuffer buff = new SimpleByteBuffer();
		for (TLVObject tlvObj : tlvObjs) {
			buff.append(tlvObj.getTag()); // tag
			buff.append(getTLVLength(tlvObj)); // len
			buff.append(tlvObj.getValue()); // value
		}

		return buff.readRemaining();
	}

	/**
	 * 解析TLV域值，得到TLV字段
	 * 
	 * @param tlvData
	 * @return 解析得到的TLV字段数
	 */
	public int unpack(byte[] tlvData) {
		int cnt = 0;

		SimpleByteBuffer buff = new SimpleByteBuffer(tlvData);
		while (buff.remaining() > 0) {
			TLVObject tlvObj = new TLVObject();

			// tag标签的属性为bit，由16进制表示，占1~2个字节长度
			byte[] tag = buff.read(1);
			if ((tag[0] & 0x0F) == 0x0F) {
				// 若tag标签的左边第一个字节的后四个bit为“1111”，表示该tag占两个字节。
				tag = buff.move(-1).read(2);
			}
			tlvObj.setTag(tag);

			// length
			byte[] len = buff.read(1);
			if ((len[0] & 0x80) == 0x80) {
				// 若length的左边第一个字节的第一个bit为“1”，表示该length实际占用字节数为bit2~bit8对应十进制数。
				len = buff.read(len[0] & 0x7F);
			}

			int length = (int) ByteUtil.toLong(len);
			if (length > 0) {
				tlvObj.setValue(buff.read(length)); // value
			}

			tlvObjs.add(tlvObj);
			cnt++;
		}

		return cnt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (TLVObject tlvObj : tlvObjs) {
			if (str.length() > 0) {
				str.append(",");
			}

			str.append(tlvObj.toString(true));
		}

		return str.toString();
	}
}
