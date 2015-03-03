package me.andpay.ti.util;

import java.io.Serializable;

/**
 * 简单字符串缓冲类 (非线程安全)
 * 
 * @author alex
 */
public class SimpleStringBuffer implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 字符串初始化容量
	 */
	private static final int INIT_CAPACITY = 16;

	/**
	 * 读取位置
	 */
	private int readPosition = 0;

	/**
	 * 数据内容
	 */
	private StringBuilder value;

	/**
	 * 构造函数
	 */
	public SimpleStringBuffer() {
		this(INIT_CAPACITY);
	}

	/**
	 * 构造函数，指定初始化容量
	 * 
	 * @param initCapacity
	 */
	public SimpleStringBuffer(int initCapacity) {
		if (initCapacity < 0) {
			throw new IllegalArgumentException("Capacity must be greater than zero");
		}

		value = new StringBuilder(Math.max(initCapacity, 16));
	}

	/**
	 * 构造函数
	 * 
	 * @param val
	 */
	public SimpleStringBuffer(String val) {
		this(val != null ? val.length() : INIT_CAPACITY);
		append(val);
	}

	/**
	 * 添加val字符串，如果val为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleStringBuffer append(String val) {
		if (val != null) {
			// append(String) efficient
			value.append(val);
		}

		return this;
	}

	/**
	 * 添加val对象的字符串，如果val为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleStringBuffer append(Object val) {
		if (val != null) {
			// append(Object)
			value.append(val);
		}

		return this;
	}

	/**
	 * 添加buff缓冲类内容，如果buff为null则忽略
	 * 
	 * @param val
	 * @return
	 */
	public SimpleStringBuffer append(SimpleStringBuffer buff) {
		if (buff != null) {
			value.append(buff.value);
		}

		return this;
	}

	/**
	 * 读取指定长度内容，并将位置标记至读取数据末尾
	 * 
	 * @param len
	 * @return
	 */
	public String read(int len) {
		if (len <= 0 || readPosition + len > value.length()) {
			throw new IllegalArgumentException(
					"The length for reading must be in [position, position + length), length=" + len);
		}

		String val = value.substring(readPosition, readPosition + len);
		readPosition += len;

		return val;
	}

	/**
	 * 尝试读取指定长度内容，如果有足够内容则读取，并将位置标志至读取数据末尾。没有足够内容则返回null。
	 * 
	 * @param len
	 * @return
	 */
	public String tryRead(int len) {
		if (len <= 0) {
			throw new IllegalArgumentException("The length must be greater than zero, length=" + len);
		}

		String val = null;
		if (readPosition + len <= value.length()) {
			val = value.substring(readPosition, readPosition + len);
			readPosition += len;
		}

		return val;
	}

	/**
	 * 从当前位置开始查找目标字符串第1次出现的位置，如果找到，则返回从当前位置到目标开始位置之间所有内容，并将当前位置标记至于目标开始或结束位置。<br/>
	 * 如果目标字符串未找到或出现次数未达到指定值，则返回null且不会改变当前位置。
	 * 
	 * @param target
	 * @param moveToTargetEnd
	 *            false: 将当前标记至于目标开始位置，true: 将当前标记至于目标结束位置
	 * @return
	 */
	public String find(String target, boolean moveToTargetEnd) {
		return find(target, 1, moveToTargetEnd);
	}

	/**
	 * 从当前位置开始查找目标字符串第N次出现的位置。 如果找到，则返回从当前位置到目标开始位置之间所有内容，并将当前位置标记至于目标开始或结束位置。<br/>
	 * 如果目标字符串未找到或出现次数未达到指定值，则返回null且不会改变当前位置。
	 * 
	 * @param target
	 * @param occurTimes
	 * @param moveToTargetEnd
	 *            false: 将当前标记至于目标开始位置，true: 将当前标记至于目标结束位置
	 * @return
	 */
	public String find(String target, int occurTimes, boolean moveToTargetEnd) {
		int targetPosition = indexOf(target, readPosition, occurTimes);
		if (targetPosition == -1) {
			return null;
		}

		String val = value.substring(readPosition, targetPosition);
		readPosition = (moveToTargetEnd ? targetPosition + target.length() : targetPosition);

		return val;
	}

	/**
	 * 读取剩余内容
	 * 
	 * @return
	 */
	public String readRemaining() {
		if (value.length() == 0) {
			return null;
		}

		String val = value.substring(readPosition);
		readPosition = value.length();

		return val;
	}

	/**
	 * 剩余可读字符数
	 * 
	 * @return
	 */
	public int remaining() {
		return value.length() - readPosition;
	}

	/**
	 * 获取指定位置的字符，不受读取位置限制
	 * 
	 * @param position
	 * @return
	 */
	public char charAt(int position) {
		if (position < 0 || position >= value.length()) {
			throw new IllegalArgumentException("Position must be in [0, length), position=" + position);
		}

		return value.charAt(position);
	}

	/**
	 * 获取指定范围[start, end)的字符串，不受读取位置限制
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public String subString(int start, int end) {
		if (start < 0 || end < start || end > value.length()) {
			throw new IllegalArgumentException(String.format(
					"Start must be in [0, end), end must be in (start, length], start=%d, end=%d", start, end));
		}

		return value.substring(start, end);
	}

	/**
	 * 查找目标字符串第1次出现的位置，不受读取位置限制
	 * 
	 * @param target
	 * @return
	 */
	public int indexOf(String target) {
		return indexOf(target, 0, 1);
	}

	/**
	 * 从指定位置开始查找目标字符串第1次出现的位置，不受读取位置限制
	 * 
	 * @param target
	 * @param fromPosition
	 * @return
	 */
	public int indexOf(String target, int fromPosition) {
		return indexOf(target, fromPosition, 1);
	}

	/**
	 * 从指定位置开始查找目标字符串出现第N次的位置，不受读取位置限制
	 * 
	 * @param target
	 * @param fromPosition
	 * @param occurTimes
	 * @return
	 */
	public int indexOf(String target, int fromPosition, int occurTimes) {
		if (target == null) {
			throw new IllegalArgumentException("The target cannot be null");

		} else if (fromPosition < 0) {
			throw new IllegalArgumentException("The fromPosition must be equal to or greater than zero");

		} else if (occurTimes <= 0) {
			throw new IllegalArgumentException("The occur times must be greater than zero, occurTimes=" + occurTimes);
		}

		if (value.length() == 0 || target.length() == 0 || target.length() + fromPosition > value.length()) {
			return -1;
		}

		while (occurTimes-- > 0) {
			int pos = value.indexOf(target, fromPosition);
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
	public SimpleStringBuffer rewind() {
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
	public SimpleStringBuffer setReadPosition(int position) {
		if (position < 0 || position >= value.length()) {
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
	public SimpleStringBuffer move(int distance) {
		int newPos = readPosition + distance;
		if (newPos < 0 || newPos >= value.length()) {
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
	public SimpleStringBuffer clear() {
		value.delete(0, value.length());
		readPosition = 0;

		return this;
	}

	/**
	 * 获取当前数据长度
	 */
	public int length() {
		return value.length();
	}

	/**
	 * 获得当前内容字符串
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return value.toString();
	}
}
