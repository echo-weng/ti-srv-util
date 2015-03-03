package me.andpay.ti.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * 简单字符缓冲类测试类
 * 
 * @author alex
 */
public class SimpleStringBufferTest {
	@Test
	public void test() {
		// 1
		SimpleStringBuffer buff = new SimpleStringBuffer();

		// append (Object, null[忽略], String, SimpleStringBuffer)
		buff.append(1234567890L).append((String) null).append("abcd").append(new SimpleStringBuffer("efgh"));

		// length
		Assert.assertEquals(18, buff.length());

		// read & getReadPosition & tryRead
		Assert.assertEquals("12", buff.read(2));
		Assert.assertEquals(2, buff.getReadPosition());
		Assert.assertEquals("3", buff.tryRead(1));
		Assert.assertEquals(3, buff.getReadPosition());
		Assert.assertEquals("45678", buff.read(5));
		Assert.assertEquals(8, buff.getReadPosition());

		// readRemaining
		Assert.assertEquals("90abcdefgh", buff.readRemaining());
		Assert.assertEquals(18, buff.getReadPosition());

		// setReadPosition
		Assert.assertEquals("678", buff.setReadPosition(5).read(3));

		// remaining
		Assert.assertEquals(10, buff.remaining());

		// rewind
		Assert.assertEquals("123", buff.rewind().read(3));

		// move
		Assert.assertEquals("678", buff.move(2).read(3));
		Assert.assertEquals("5678", buff.move(-4).read(4));

		// charAt
		Assert.assertEquals('1', buff.charAt(0));

		// subString
		Assert.assertEquals("45", buff.subString(3, 5));

		// init with String
		buff = new SimpleStringBuffer(buff.subString(0, buff.length()));

		// toString
		Assert.assertEquals("1234567890abcdefgh", buff.toString());

		// init specified capacity
		buff = new SimpleStringBuffer(1024);

		// extend capacity 1024
		Assert.assertEquals(1024, buff.append(StringUtil.rightPad("", "0", 1024)).toString().length());

		// tryRead
		Assert.assertEquals(1024, buff.rewind().tryRead(1024).length());

		// clear
		Assert.assertEquals(0, buff.clear().length());

		// tryRead
		Assert.assertNull(buff.tryRead(100));

		// reset
		buff.clear().append("1234567890123456");

		// indexOf(target)
		Assert.assertEquals(0, buff.indexOf("12"));
		Assert.assertEquals(6, buff.indexOf("7"));
		Assert.assertEquals(-1, buff.indexOf("a"));

		// indexOf(target, fromPos)
		Assert.assertEquals(0, buff.indexOf("12", 0));
		Assert.assertEquals(10, buff.indexOf("12", 1));
		Assert.assertEquals(10, buff.indexOf("12", 10));
		Assert.assertEquals(-1, buff.indexOf("12", 11));
		Assert.assertEquals(6, buff.indexOf("7", 0));
		Assert.assertEquals(6, buff.indexOf("7", 6));
		Assert.assertEquals(-1, buff.indexOf("7", 7));
		Assert.assertEquals(-1, buff.indexOf("a", 0));

		// indexOf(target, fromPos, occurTimes)
		Assert.assertEquals(0, buff.indexOf("12", 0, 1));
		Assert.assertEquals(10, buff.indexOf("12", 1, 1));
		Assert.assertEquals(10, buff.indexOf("12", 10, 1));
		Assert.assertEquals(10, buff.indexOf("12", 0, 2));
		Assert.assertEquals(-1, buff.indexOf("12", 1, 2));
		Assert.assertEquals(-1, buff.indexOf("12", 0, 3));
		Assert.assertEquals(6, buff.indexOf("7", 0, 1));
		Assert.assertEquals(6, buff.indexOf("7", 6, 1));
		Assert.assertEquals(-1, buff.indexOf("7", 7, 1));
		Assert.assertEquals(-1, buff.indexOf("7", 0, 2));
		Assert.assertEquals(-1, buff.indexOf("a", 0, 1));

		// find(target, moveToTargetEnd=false)
		Assert.assertEquals("1", buff.rewind().find("23", false));
		Assert.assertEquals("2", buff.find("34", false));
		Assert.assertEquals("345678901", buff.find("23", false));
		Assert.assertNull(buff.find("1", false));

		// find(target, moveToTargetEnd=true)
		Assert.assertEquals("", buff.rewind().find("12", true));
		Assert.assertEquals("34567890", buff.find("12", true));
		Assert.assertNull(buff.find("12", false));

		// find(target, occurTimes, moveToTargetEnd=false)
		Assert.assertEquals("1234567890", buff.rewind().find("12", 2, false));
		Assert.assertEquals("", buff.find("12", 1, false));
		Assert.assertNull(buff.move(1).find("12", 1, false));

		// find(target, occurTimes, moveToTargetEnd=true)
		Assert.assertEquals("1234567890", buff.rewind().find("12", 2, true));
		Assert.assertEquals("", buff.find("34", 1, true));
		Assert.assertNull(buff.find("12", 1, true));

		// clear
		buff.clear();

		try {
			// not data to read
			buff.read(1);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}

		try {
			// invalid len
			buff.tryRead(0);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}

		try {
			// invalid position
			buff.setReadPosition(1);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}

		try {
			// invalid args
			buff.indexOf(null, -1, -1);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}

		try {
			// invalid args
			buff.find(null, -1, false);
			Assert.fail("Expected exception");
		} catch (Exception e) {
		}
	}
}
