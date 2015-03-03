package me.andpay.ti.util;

import static me.andpay.ti.util.ExHelper.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import me.andpay.ti.base.AppBizException;
import me.andpay.ti.base.AppRtException;

import org.junit.Test;

/**
 * 异常帮助类测试类。
 * 
 * @author sea.bao
 */
public class ExHelperTest {
	@Test
	public void testNewAppBizExString() {
		try {
			throw newAppBizEx("001");
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001].", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppBizEx(new Exception(), "001");
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001].", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppBizExStringString() {
		try {
			throw newAppBizEx("001", "Error Message.");
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppBizEx(new Exception(), "001", "Error Message.");
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppBizExStringStringVarArgs() {
		try {
			throw newAppBizEx("001", "Error Message, #{a0}=#{f('%02d', a1)}.", "id", 1);
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message, id=01.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppBizEx(new Exception(), "001", "Error Message, #{a0}=#{f'%02d', a1)}.", "id", 1);
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message, #{a0}=#{f'%02d', a1)}.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppBizExStringStringMap() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("name", "id");
		ctx.put("id", 1);

		try {
			throw newAppBizEx("001", "Error Message, #{name}=#{f('%02d', id)}.", ctx);
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message, id=01.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppBizEx(new Exception(), "001", "Error Message, #{a0}=#{f'%02d', a1)}.", ctx);
		} catch (AppBizException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppBizException[001]: Error Message, #{a0}=#{f'%02d', a1)}.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppRtExString() {
		try {
			throw newAppRtEx("001");
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001].", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppRtEx(new Exception(), "001");
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001].", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppRtExStringString() {
		try {
			throw newAppRtEx("001", "Error Message.");
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppRtEx(new Exception(), "001", "Error Message.");
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppRtExStringStringVarArgs() {
		try {
			throw newAppRtEx("001", "Error Message, #{a0}=#{f('%02d', a1)}.", "id", 1);
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message, id=01.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppRtEx(new Exception(), "001", "Error Message, #{a0}=#{f'%02d', a1)}.", "id", 1);
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message, #{a0}=#{f'%02d', a1)}.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testNewAppRtExStringStringMap() {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("name", "id");
		ctx.put("id", 1);

		try {
			throw newAppRtEx("001", "Error Message, #{name}=#{f('%02d', id)}.", ctx);
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message, id=01.", e.getMessage());
			assertNull(e.getCause());
		}

		try {
			throw newAppRtEx(new Exception(), "001", "Error Message, #{a0}=#{f'%02d', a1)}.", ctx);
		} catch (AppRtException e) {
			assertEquals("001", e.getCode());
			assertEquals("AppRtException[001]: Error Message, #{a0}=#{f'%02d', a1)}.", e.getMessage());
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testGetAppExcCode() {
		assertEquals("BIZ.001", getAppExcCode(new AppBizException("BIZ.001"), "BIZ.002"));
		assertEquals("RT.001", getAppExcCode(new AppRtException("RT.001"), "RT.002"));
		assertEquals("BIZ.002", getAppExcCode(new AppBizException(null), "BIZ.002"));
		assertEquals("RT.002", getAppExcCode(new RuntimeException(), "RT.002"));
		assertEquals("BIZ.003", getAppExcCode(null, "BIZ.003"));
	}

	@Test
	public void testWrapAppBizEx() {
		assertEquals("BIZ.001", wrapAppBizEx(new AppBizException("BIZ.001"), "BIZ.002").getCode());
		assertEquals("RT.001", wrapAppBizEx(new AppRtException("RT.001"), "RT.002").getCode());
		assertNull("BIZ.002", wrapAppBizEx(new AppBizException(null), "BIZ.002").getCode());
		assertEquals("RT.002", wrapAppBizEx(new AppRtException(null), "RT.002").getCode());
		assertEquals("RT.003", wrapAppBizEx(new RuntimeException(), "RT.003").getCode());
		assertEquals("BIZ.003", wrapAppBizEx(null, "BIZ.003").getCode());
	}

	@Test
	public void testWrapAppRtEx() {
		assertEquals("BIZ.001", wrapAppRtEx(new AppBizException("BIZ.001"), "BIZ.002").getCode());
		assertEquals("RT.001", wrapAppRtEx(new AppRtException("RT.001"), "RT.002").getCode());
		assertNull(wrapAppRtEx(new AppRtException(null), "RT.002").getCode());
		assertEquals("BIZ.002", wrapAppRtEx(new AppBizException(null), "BIZ.002").getCode());
		assertEquals("RT.003", wrapAppRtEx(new RuntimeException(), "RT.003").getCode());
		assertEquals("BIZ.003", wrapAppRtEx(null, "BIZ.003").getCode());
	}

	@Test
	public void testGetAppExcCtx() {
		final Map<String, String> context = new HashMap<String, String>();

		assertNotNull(getAppExcCtx(new AppBizException("BIZ.001", "test", context)));
		assertNotNull(getAppExcCtx(new AppRtException("BIZ.001", "test", context)));
		assertTrue(getAppExcCtx(new AppBizException("BIZ.001", "test")).isEmpty());
		assertTrue(getAppExcCtx(new AppRtException("BIZ.001", "test")).isEmpty());
		assertNull(getAppExcCtx(new Exception()));
		assertNull(getAppExcCtx(null));
	}

	@Test
	public void testGetAppExcCtxPara() {
		final Map<String, String> context = new HashMap<String, String>();
		context.put("a", "1");

		assertEquals("1", getAppExcCtxPara(new AppBizException("BIZ.001", "test", context), "a"));
		assertEquals("1", getAppExcCtxPara(new AppRtException("BIZ.001", "test", context), "a"));
		assertNull(getAppExcCtxPara(new AppBizException("BIZ.001", "test", context), "b"));
		assertNull(getAppExcCtxPara(new AppRtException("BIZ.001", "test", context), "b"));
		assertNull(getAppExcCtxPara(new AppBizException("BIZ.001", "test"), "a"));
		assertNull(getAppExcCtxPara(new AppRtException("BIZ.001", "test"), "a"));
		assertNull(getAppExcCtxPara(new Exception(), "a"));
		assertNull(getAppExcCtxPara(null, "a"));
	}
}
