package me.andpay.ti.util;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * 脚本模板测试类。
 * 
 * @author sea.bao
 */
public class ScriptTemplateTest {

	@Test
	public void test() {
		ScriptTemplate scriptTemplate = new ScriptTemplate();
		String[] scriptHeaders = new String[2];
		scriptHeaders[0] = "import java.util.*;";
		scriptHeaders[1] = "import java.math.*;";
		String script = "Date d = new Date();\nBigDecimal m = new BigDecimal(1);\nprintf('1');\nreturn r;";
		ScriptObject scriptObj = scriptTemplate.prepareScript("groovy", scriptHeaders, script, "test.groovy");
		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("r", 1L);
		Long ret = (Long )scriptTemplate.evalScript(scriptObj, bindings);
		assertTrue(1L == ret);
	}

}
