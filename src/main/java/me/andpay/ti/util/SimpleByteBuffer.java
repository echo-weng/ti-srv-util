package me.andpay.ti.util;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 简单字节缓冲类 (非线程安全)
 * 
 * @author alex
 */
public class SimpleByteBuffer implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 字节数组初始化容量
	 */
	private static final int INIT_CAPACITY = 16;

	/**
	 * 读取位置
	 */
	private int readPosition = 0;

	/**
	 * 实际数据长度
	 */
	private int length = 0;

	/**
	 * 数据内容
	 */
	private byte[] value;

	/**
	 * 构造函数
	 */
	public SimpleByteBuffer() {
		this(INIT_CAPACITY);
	}

	/**
	 * 构造函数，指定初始化容量
	 * 
	 * @param initCapacity
	 */
	public SimpleByteBuffer(int initCapacity) {
		if (initCapacity < 0) {
			throw new IllegalArgumentException("Capacity must be greater than zero");
		}

		value = new byte[Math.max(initCapacity, INIT_CAPACITY)];
	}

	/**
	 * 构造函数
	 * 
	 * @param val
	 */
	public SimpleByteBuffer(byte[] val) {
		this(val != null ? val.length : INIT_CAPACITY);
		append(val);
	}

	/**
	 * 添加val字节，如果val为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleByteBuffer append(byte val) {
		return append(new byte[] { val });
	}

	/**
	 * 添加val字节数组，如果val为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleByteBuffer append(byte[] val) {
		if (val != null && val.length > 0) {
			ensureCapacity(length + val.length);
			System.arraycopy(val, 0, value, length, val.length);
			length += val.length;
		}

		return this;
	}

	/**
	 * 添加val对象字符串(指定字符集)转换的字节数组，如果val为null则忽略
	 * 
	 * @param val
	 * @param charset
	 * @return
	 */
	public SimpleByteBuffer append(Object val, String charset) {
		if (val != null) {
			append(val.toString().getBytes(Charset.forName(charset)));
		}

		return this;
	}

	/**
	 * 添加buff字节缓冲数据，如果buff为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleByteBuffer append(SimpleByteBuffer buff) {
		if (buff != null && buff.length > 0) {
			ensureCapacity(length + buff.length);
			System.arraycopy(buff.value, 0, value, length, buff.length);
			length += buff.length;
		}

		return this;
	}

	/**
	 * 确保内部数据容量足够满足指定大小
	 * 
	 * @param minCapacity
	 */
	private void ensureCapacity(int minCapacity) {
		if (minCapacity > value.length) {
			// extend capacity
			value = Arrays.copyOf(value, Math.max((value.length * 3) / 2 + 1, minCapacity));
		}
	}

	/**
	 * 读取指定长度内容，并将位置标记至读取数据末尾
	 * 
	 * @param len
	 * @return
	 */
	public byte[] read(int len) {
		if (len <= 0 || readPosition + len > length) {
			throw new IllegalArgumentException(
					"The length for reading must be in [position, position + length), length=" + len);
		}

		byte[] val = new byte[len];
		System.arraycopy(value, readPosition, val, 0, val.length);
		readPosition += len;

		return val;
	}

	/**
	 * 尝试读取指定长度内容，如果有足够内容则读取，并将位置标志至读取数据末尾。没有足够内容则返回null。
	 * 
	 * @param len
	 * @return
	 */
	public byte[] tryRead(int len) {
		if (len <= 0) {
			throw new IllegalArgumentException("The length must be greater than zero, length=" + len);
		}

		byte[] val = null;
		if (readPosition + len <= length) {
			val = new byte[len];
			System.arraycopy(value, readPosition, val, 0, val.length);
			readPosition += len;
		}

		return val;
	}

	/**
	 * 从当前位置开始查找目标字节数组第1次出现的位置，如果找到，则返回从当前位置到目标开始位置之间所有内容，并将当前位置标记至于目标开始或结束位置。<br/>
	 * 如果目标字节数组未找到或出现次数未达到指定值，则返回null且不会改变当前位置。
	 * 
	 * @param target
	 * @param moveToTargetEnd
	 *            false: 将当前标记至于目标开始位置，true: 将当前标记至于目标结束位置
	 * @return
	 */
	public byte[] find(byte[] target, boolean moveToTargetEnd) {
		return find(target, 1, moveToTargetEnd);
	}

	/**
	 * 从当前位置开始查找目标字节数组第N次出现的位置，如果找到，则返回从当前位置到目标开始位置之间所有内容，并将当前位置标记至于目标开始或结束位置。<br/>
	 * 如果目标字节数组未找到或出现次数未达到指定值，则返回null且不会改变当前位置。
	 * 
	 * @param target
	 * @param occurTimes
	 * @param moveToTargetEnd
	 *            false: 将当前标记至于目标开始位置，true: 将当前标记至于目标结束位置
	 * @return
	 */
	public byte[] find(byte[] target, int occurTimes, boolean moveToTargetEnd) {
		int targetPosition = indexOf(target, readPosition, occurTimes);
		if (targetPosition == -1) {
			return null;
		}

		byte[] val = new byte[targetPosition - readPosition];
		System.arraycopy(value, readPosition, val, 0, val.length);
		readPosition = (moveToTargetEnd ? targetPosition + target.length : targetPosition);

		return val;
	}

	/**
	 * 读取剩余内容，并将位置标记至数据末尾
	 * 
	 * @return
	 */
	public byte[] readRemaining() {
		if (length == 0) {
			return null;
		}

		byte[] val = new byte[length - readPosition];
		System.arraycopy(value, readPosition, val, 0, val.length);
		readPosition = length;

		return val;
	}

	/**
	 * 剩余可读字节数
	 * 
	 * @return
	 */
	public int remaining() {
		return length - readPosition;
	}

	/**
	 * 获取指定位置的字符，不受读取位置限制
	 * 
	 * @param position
	 * @return
	 */
	public byte byteAt(int position) {
		if (position < 0 || position >= length) {
			throw new IllegalArgumentException("Position must be in [0, length), position=" + position);
		}

		return value[position];
	}

	/**
	 * 获取指定范围[start, end)的字节数组，不受读取位置限制
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public byte[] subBytes(int start, int end) {
		if (start < 0 || end < start || end > length) {
			throw new IllegalArgumentException(String.format(
					"Start must be in [0, end), end must be in (start, length], start=%d, end=%d", start, end));
		}

		byte[] val = new byte[end - start];
		System.arraycopy(value, start, val, 0, val.length);

		return val;
	}

	/**
	 * 查找目标字节数组第1次出现的位置，不受读取位置限制
	 * 
	 * @param target
	 * @return
	 */
	public int indexOf(byte[] target) {
		return indexOf(target, 0, 1);
	}

	/**
	 * 从指定位置开始查找目标字节数组第1次出现的位置，不受读取位置限制
	 * 
	 * @param target
	 * @param fromPosition
	 * @return
	 */
	public int indexOf(byte[] target, int fromPosition) {
		return indexOf(target, fromPosition, 1);
	}

	/**
	 * 从指定位置开始查找目标字节数组出现第N次的位置，不受读取位置限制
	 * 
	 * @param target
	 * @param fromPosition
	 * @param occurTimes
	 * @return
	 */
	public int indexOf(byte[] target, int fromPosition, int occurTimes) {
		if (target == null) {
			throw new IllegalArgumentException("The target cannot be null");

		} else if (fromPosition < 0) {
			throw new IllegalArgumentException("The fromPosition must be equal to or greater than zero");

		} else if (occurTimes <= 0) {
			throw new IllegalArgumentException("The occur times must be greater than zero, occurTimes=" + occurTimes);
		}

		if (length == 0 || target.length == 0 || target.length + fromPosition > length) {
			return -1;
		}

		int endPosition = length - target.length;
		while (occurTimes-- > 0) {
			int pos = -1;
			next: for (int i = fromPosition; i <= endPosition; i++) {
				for (int j = 0; j < target.length; j++) {
					if (value[i + j] != target[j]) {
						continue next;
					}
				}

				pos = i;
				break;
			}

			if (pos == -1) {
				return -1; // not found
			}

			fromPosition = pos + 1; // found
		}

		return fromPosition - 1; // last pos
	}

	/**
	 * 将当前读取位置设置为0
	 * 
	 * @return
	 */
	public SimpleByteBuffer rewind() {
		readPosition = 0;
		return this;
	}

	/**
	 * 返回当前读取位置
	 * 
	 * @return
	 */
	public int getReadPosition() {
		return readPosition;
	}

	/**
	 * 将当前读取位置设置至指定位置
	 * 
	 * @param position
	 * @return
	 */
	public SimpleByteBuffer setReadPosition(int position) {
		if (position < 0 || position >= length) {
			throw new IllegalArgumentException("Position must be in [0, length), position=" + position);
		}
		readPosition = position;
		return this;
	}

	/**
	 * 将当前读取位置移动指定的距离，正数表示往前移动，负数表示往后移动
	 * 
	 * @param distance
	 * @return
	 */
	public SimpleByteBuffer move(int distance) {
		int newPos = readPosition + distance;
		if (newPos < 0 || newPos >= length) {
			throw new IllegalArgumentException("The distance must be in [-position, length - position), distance="
					+ distance);
		}
		readPosition = newPos;
		return this;
	}

	/**
	 * 清空所有数据
	 * 
	 * @return
	 */
	public SimpleByteBuffer clear() {
		value = new byte[INIT_CAPACITY];
		readPosition = 0;
		length = 0;

		return this;
	}

	/**
	 * 获取当前数据长度
	 */
	public int length() {
		return length;
	}

	/**
	 * 获得当前内容的HEX字符串
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(value[i] & 0xFF);
			if (hex.length() == 1) {
				str.append('0');
			}
			str.append(hex);
		}

		return str.toString().toUpperCase();
	}
}
