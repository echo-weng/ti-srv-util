package me.andpay.ti.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * SimplePropertyParser测试类
 * 
 * @author alex
 */
public class SimplePropertyParserTest {

	private SimplePropertyParser<Map<String, String>> parser = new SimplePropertyParser<Map<String, String>>() {
		@Override
		protected Map<String, String> newPropertyHolder(String scope, String category) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("_s", scope);
			map.put("_c", category);
			return map;
		}

		@Override
		protected void addProperty(Map<String, String> propHolder, String name, String value) {
			propHolder.put(name, value);
		}

		@Override
		protected String getDefaultScope() {
			return null;
		};
	};

	@Test
	public void test() throws IOException {
		List<Map<String, String>> maps = parser.parse(new FileInputStream("src/test/resources/test-data/test.props"));

		Assert.assertEquals(3, maps.size());

		Map<String, String> map = maps.get(0);
		Assert.assertEquals("ac-test-1", map.get("_s"));
		Assert.assertEquals("app_exc_code", map.get("_c"));
		Assert.assertEquals(2 + 2, map.size());
		Assert.assertEquals("sys err 1", map.get("SYS.001"));
		Assert.assertEquals("sys err 2", map.get("SYS.002"));

		map = maps.get(1);
		Assert.assertEquals("ac-test-2", map.get("_s"));
		Assert.assertEquals("app_exc_code", map.get("_c"));
		Assert.assertEquals(2 + 2, map.size());
		Assert.assertEquals("system error 1", map.get("SYS.001"));
		Assert.assertEquals("system error 2", map.get("SYS.002"));

		map = maps.get(2);
		Assert.assertNull(map.get("_s"));
		Assert.assertEquals("resp_msg", map.get("_c"));
		Assert.assertEquals(2 + 3, map.size());
		Assert.assertEquals("交易成功", map.get("TXN.000"));
		Assert.assertEquals("system error", map.get("SYS.001"));
		Assert.assertEquals("system error \\", map.get("SYS.002"));
	}
}
