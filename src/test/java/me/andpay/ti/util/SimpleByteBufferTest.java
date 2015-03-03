package me.andpay.ti.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * 简单字节缓冲类测试类
 * 
 * @author alex
 */
public class SimpleByteBufferTest {
	@Test
	public void test() {
		// 1
		SimpleByteBuffer buff = new SimpleByteBuffer();

		// append (byte[], byte)
		buff.append(new byte[] { 0x12, 0x34 }).append((byte) 0x56);

		// append (SimpleByteBuffer, null[忽略])
		buff.append(new SimpleByteBuffer(new byte[] { 0x78, (byte) 0x90 })).append((byte[]) null);

		// append (Object 31, 0A)
		buff.append(1, Charsets.ISO_8859_1).append('\n', Charsets.ISO_8859_1);

		// length
		Assert.assertEquals(7, buff.length());

		// read & getReadPosition & tryRead
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34 }, buff.read(2));
		Assert.assertEquals(2, buff.getReadPosition());
		Assert.assertArrayEquals(new byte[] { 0x56 }, buff.tryRead(1));
		Assert.assertEquals(3, buff.getReadPosition());

		// readRemaining
		Assert.assertArrayEquals(new byte[] { 0x78, (byte) 0x90, 0x31, 0x0A }, buff.readRemaining());
		Assert.assertEquals(7, buff.getReadPosition());

		// setReadPosition
		Assert.assertArrayEquals(new byte[] { 0x34 }, buff.setReadPosition(1).read(1));

		// remaining
		Assert.assertEquals(5, buff.remaining());

		// rewind
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34 }, buff.rewind().read(2));

		// move
		Assert.assertArrayEquals(new byte[] { (byte) 0x90 }, buff.move(2).read(1));
		Assert.assertArrayEquals(new byte[] { 0x78, (byte) 0x90 }, buff.move(-2).read(2));

		// charAt
		Assert.assertEquals(0x12, buff.byteAt(0));

		// subString
		Assert.assertArrayEquals(new byte[] { 0x78, (byte) 0x90 }, buff.subBytes(3, 5));

		// init specified bytes
		buff = new SimpleByteBuffer(buff.subBytes(0, buff.length()));

		// toString
		Assert.assertEquals("1234567890310A", buff.toString());

		// init specified capacity
		buff = new SimpleByteBuffer(1024);

		// extend capacity 1024
		Assert.assertEquals(1024, buff.append(new byte[1024]).subBytes(0, buff.length()).length);
		Assert.assertEquals(1024 * 2, buff.toString().length());

		// tryRead
		Assert.assertEquals(1024, buff.rewind().tryRead(1024).length);

		// clear
		Assert.assertEquals(0, buff.clear().length());

		// tryRead
		Assert.assertNull(buff.tryRead(100));

		// reset
		buff.clear().append(new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x12, 0x34, 0x56 });

		// indexOf(target)
		Assert.assertEquals(0, buff.indexOf(new byte[] { 0x12, 0x34 }));
		Assert.assertEquals(3, buff.indexOf(new byte[] { 0x78 }));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { (byte) 0xFF }));

		// indexOf(target, fromPos)
		Assert.assertEquals(0, buff.indexOf(new byte[] { 0x12, 0x34 }, 0));
		Assert.assertEquals(5, buff.indexOf(new byte[] { 0x12, 0x34 }, 1));
		Assert.assertEquals(5, buff.indexOf(new byte[] { 0x12, 0x34 }, 5));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x12, 0x34 }, 6));
		Assert.assertEquals(3, buff.indexOf(new byte[] { 0x78 }, 0));
		Assert.assertEquals(3, buff.indexOf(new byte[] { 0x78 }, 3));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x78 }, 4));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { (byte) 0xFF }, 0));

		// indexOf(target, fromPos, occurTimes)
		Assert.assertEquals(0, buff.indexOf(new byte[] { 0x12, 0x34 }, 0, 1));
		Assert.assertEquals(5, buff.indexOf(new byte[] { 0x12, 0x34 }, 1, 1));
		Assert.assertEquals(5, buff.indexOf(new byte[] { 0x12, 0x34 }, 5, 1));
		Assert.assertEquals(5, buff.indexOf(new byte[] { 0x12, 0x34 }, 0, 2));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x12, 0x34 }, 1, 2));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x12, 0x34 }, 0, 3));
		Assert.assertEquals(3, buff.indexOf(new byte[] { 0x78 }, 0, 1));
		Assert.assertEquals(3, buff.indexOf(new byte[] { 0x78 }, 3, 1));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x78 }, 4, 1));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { 0x78 }, 0, 2));
		Assert.assertEquals(-1, buff.indexOf(new byte[] { (byte) 0xFF }, 0, 1));

		// find(target, moveToTargetEnd=false)
		Assert.assertArrayEquals(new byte[] { 0x12 }, buff.rewind().find(new byte[] { 0x34, 0x56 }, false));
		Assert.assertArrayEquals(new byte[] { 0x34 }, buff.find(new byte[] { 0x56, 0x78 }, false));
		Assert.assertArrayEquals(new byte[] { 0x56, 0x78, (byte) 0x90, 0x12 },
				buff.find(new byte[] { 0x34, 0x56 }, false));
		Assert.assertNull(buff.find(new byte[] { 0x12 }, false));

		// find(target, moveToTargetEnd=true)
		Assert.assertArrayEquals(new byte[0], buff.rewind().find(new byte[] { 0x12, 0x34 }, true));
		Assert.assertArrayEquals(new byte[] { 0x56, 0x78, (byte) 0x90 }, buff.find(new byte[] { 0x12, 0x34 }, true));
		Assert.assertNull(buff.find(new byte[] { 0x12 }, false));

		// find(target, occurTimes, moveToTargetEnd=false)
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90 },
				buff.rewind().find(new byte[] { 0x12, 0x34 }, 2, false));
		Assert.assertArrayEquals(new byte[0], buff.find(new byte[] { 0x12, 0x34 }, 1, false));
		Assert.assertNull(buff.move(1).find(new byte[] { 0x12, 0x34 }, 1, false));

		// find(target, occurTimes, moveToTargetEnd=true)
		Assert.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78, (byte) 0x90 },
				buff.rewind().find(new byte[] { 0x12, 0x34 }, 2, true));
		Assert.assertArrayEquals(new byte[0], buff.find(new byte[] { 0x56 }, 1, true));
		Assert.assertNull(buff.find(new byte[] { 0x12, 0x34 }, 1, true));

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
