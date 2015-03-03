package me.andpay.ti.util;

import java.util.LinkedHashMap;
import java.util.Map;

import me.andpay.ti.util.ArrayUtil;
import me.andpay.ti.util.TunnelData;

import org.junit.Assert;
import org.junit.Test;

/**
 * 隧道数据对象测试类
 * 
 * @author alex
 */
public class TunnelDataTest {

	@Test
	public void testTunnelData() {
		final Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("n1", 1);
		map.put("n2", ArrayUtil.asMap("n3", "3"));

		final String mapInJson1 = "{\"n1\":1,\"n2\":{\"n3\":\"3\"}}";
		final String mapInJson2 = "{\"n1\":\"1\",\"n4\":\"4\"}";

		TunnelData tunnelData = new TunnelData();
		Assert.assertNull(tunnelData.getTunnelData());
		Assert.assertNull(tunnelData.getTunnelDataMap());

		// setTunnelDataMap
		tunnelData.setTunnelDataMap(map);
		Assert.assertEquals(mapInJson1, tunnelData.getTunnelData());
		Assert.assertSame(map, tunnelData.getTunnelDataMap());

		// getTunnelTag
		Assert.assertEquals(1, tunnelData.getTunnelTag("n1"));
		Assert.assertEquals(1, ((Map<?, ?>) tunnelData.getTunnelTag("n2")).size());
		Assert.assertNull(tunnelData.getTunnelTag("n3"));

		// setTunnelTag
		Object old = tunnelData.setTunnelTag("n4", "4");
		Assert.assertNull(old);
		old = tunnelData.setTunnelTag("n1", "1");
		Assert.assertEquals(1, old);

		// removeTunnelTag
		old = tunnelData.removeTunnelTag("n2");
		Assert.assertEquals(1, ((Map<?, ?>) old).size());
		Assert.assertSame(map, tunnelData.getTunnelDataMap());
		Assert.assertEquals(mapInJson2, tunnelData.getTunnelData());

		// setTunnelData
		tunnelData.setTunnelData(mapInJson1);
		Assert.assertEquals(mapInJson1, tunnelData.getTunnelData());
		Assert.assertEquals(1, tunnelData.getTunnelTag("n1"));
		Assert.assertNotSame(map, tunnelData.getTunnelDataMap());

		// setOrRemoveTunnelData
		Assert.assertEquals(1, tunnelData.setOrRemoveTunnelTag("n1", null));

		// containsTag
		Assert.assertFalse(tunnelData.containsTag("n1"));

		// setTunnelTagIfNotNull
		Assert.assertNull(tunnelData.setTunnelTagIfNotNull("n1", null));
		Assert.assertFalse(tunnelData.containsTag("n1"));
	}
}
