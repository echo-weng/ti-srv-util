package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import me.andpay.ti.util.SpelFormatter;

import org.junit.Test;

/**
 * 测试Spel格式化器类。
 * 
 * @author sea.bao
 */
public class SpelFormatterTest {

	@Test
	public void testFormatStringMapOfStringObject() {
		Map<String, String> args = new HashMap<String, String>();
		args.put("name", "sea.bao");
		args.put("url", "http://www.andpay.me");

		String txt = SpelFormatter.format("Hello #{url}, #{name}!", args);
		String exText = "Hello " + args.get("url") + ", " + args.get("name") + "!";
		assertEquals(exText, txt);

		Map<String, Object> args2 = new HashMap<String, Object>();
		args2.put("name", "sea.bao");
		args2.put("url", "http://www.andpay.me");
		args2.put("id", 1);
		txt = SpelFormatter.format("#{name}'s id is #{f('%02d', id)}", args2);
		assertEquals("sea.bao's id is 01", txt);
	}

	@Test
	public void testFormatStringObjectArray() {
		String txt = SpelFormatter.format("Hello #{a0}, #{a1}!", "world", "sea.bao");
		assertEquals("Hello world, sea.bao!", txt);
		
		txt = SpelFormatter.format("Hello #{a0}, #{a1}!", "world");
		System.out.println(txt);
	}

}
