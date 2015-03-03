package me.andpay.ti.iso8583;

import java.nio.charset.Charset;

import me.andpay.ti.util.Charsets;
import me.andpay.ti.util.HexUtil;

/**
 * TLV数据对象类
 * 
 * @author alex
 */
public class TLVObject {
	/**
	 * 默认字符集
	 */
	private static final String DEF_CHARSET = Charsets.GBK;

	/**
	 * 标签名
	 */
	private byte[] tag;

	/**
	 * 数据内容
	 */
	private byte[] value;

	/**
	 * 获取标签
	 * 
	 * @return
	 */
	public byte[] getTag() {
		return tag;
	}

	/**
	 * 设置标签
	 * 
	 * @param tag
	 */
	public void setTag(byte[] tag) {
		this.tag = tag;
	}

	/**
	 * 获取标签HEX字符串
	 * 
	 * @return
	 */
	public String getHexTag() {
		return HexUtil.encodeHex(tag);
	}

	/**
	 * 设置标签HEX字符串
	 * 
	 * @param tag
	 */
	public void setHexTag(String tagHex) {
		this.tag = HexUtil.decodeHex(tagHex);
	}

	/**
	 * 获取字符串值
	 * 
	 * @return
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * 获取十六进制字符串值
	 * 
	 * @return
	 */
	public String getHexValue() {
		return HexUtil.encodeHex(value);
	}

	/**
	 * 获取GBK字符集下的字符串
	 * 
	 * @return
	 */
	public String getStringValue() {
		return getStringValue(DEF_CHARSET);
	}

	/**
	 * 获取指定字符集下的字符串
	 * 
	 * @return
	 */
	public String getStringValue(String charset) {
		return (value != null ? new String(value, Charset.forName(charset)) : null);
	}

	/**
	 * 设置字符串值
	 * 
	 * @param value
	 */
	public void setValue(byte[] value) {
		this.value = value;
	}

	/**
	 * 设置十六进制字符串值
	 * 
	 * @param valueHex
	 */
	public void setHexValue(String valueHex) {
		this.value = HexUtil.decodeHex(valueHex);
	}

	/**
	 * 设置字符串值，使用默认字符集进行转换
	 * 
	 * @param value
	 */
	public void setStringValue(String value) {
		setStringValue(value, DEF_CHARSET);
	}

	/**
	 * 设置字符串值，使用指定字符集进行转换
	 * 
	 * @param value
	 * @param charset
	 */
	public void setStringValue(String value, String charset) {
		if (value == null) {
			this.value = null;
			return;
		}

		this.value = value.getBytes(Charset.forName(charset));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toString(false);
	}

	/**
	 * 转换为字符串形式
	 * 
	 * @param isHexValue
	 * @return
	 */
	public String toString(boolean isHexValue) {
		StringBuilder str = new StringBuilder();
		str.append(getHexTag());
		if (value == null) {
			str.append(":0:");

		} else {
			str.append(":").append(value.length);
			str.append(":").append(isHexValue ? getHexValue() : getStringValue());
		}

		return str.toString();
	}
}
