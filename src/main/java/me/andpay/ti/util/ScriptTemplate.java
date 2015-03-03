package me.andpay.ti.util;

import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

/**
 * 脚本模板类。
 * 
 * @author sea.bao
 */
public class ScriptTemplate {
	/**
	 * 脚本引擎集
	 */
	private Map<String, ScriptEngine> scriptEngines = new HashMap<String, ScriptEngine>();

	private ScriptEngine getScriptEngine(String scriptLang) {
		ScriptEngine se = scriptEngines.get(scriptLang);
		if (se == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
			se = manager.getEngineByName(scriptLang);
			if (se == null) {
				throw new RuntimeException("Not found the ScriptEngine for=[" + scriptLang + "].");
			}

			scriptEngines.put(scriptLang, se);
		}

		return se;
	}

	public ScriptObject prepareScript(String scriptLang, String[] scriptHeaders, String script, String scriptSrcRef) {
		StringBuffer sb = new StringBuffer();
		if (scriptHeaders != null) {
			for (String scriptHeader : scriptHeaders) {
				if (StringUtil.emptyAsNull(scriptHeader) != null) {
					sb.append(scriptHeader);
					sb.append("\n");
				}
			}
		}

		return prepareScript(scriptLang, sb.toString(), script, scriptSrcRef);
	}

	public ScriptObject prepareScript(String scriptLang, String scriptHeader, String script, String scriptSrcRef) {
		if (scriptLang == null) {
			throw new IllegalArgumentException("scriptLang is required.");
		}

		ScriptEngine se = getScriptEngine(scriptLang);

		ScriptObject scriptObj = new ScriptObject();
		scriptObj.setScriptLang(scriptLang);

		boolean scriptFlag = false;
		StringBuffer sb = new StringBuffer();
		if (StringUtil.emptyAsNull(scriptHeader) != null) {
			sb.append(scriptHeader);
			sb.append("\n");
			scriptFlag = true;
		}

		if (StringUtil.emptyAsNull(script) != null) {
			sb.append(script);
			scriptFlag = true;
		}

		if (scriptFlag == false) {
			throw new IllegalArgumentException("script is null.");
		}

		scriptObj.setScript(sb.toString());

		if (se instanceof Compilable) {
			Compilable compilable = (Compilable) se;
			CompiledScript compiledScript;
			try {
				compiledScript = compilable.compile(scriptObj.getScript());
			} catch (Throwable t) {
				throw new RuntimeException("Compile expression error, scriptSrcRef=" + scriptSrcRef, t);
			}

			scriptObj.setCompiledScript(compiledScript);
		}

		scriptObj.setScriptSrcRef(scriptSrcRef);

		return scriptObj;
	}

	public Object evalScript(ScriptObject scriptObj, Map<String, Object> bindings) {
		Bindings oBindings = new SimpleBindings(bindings);

		Object retValue;
		if (scriptObj.getCompiledScript() != null) {
			try {
				retValue = scriptObj.getCompiledScript().eval(oBindings);
			} catch (Throwable t) {
				throw new RuntimeException("Eval compiled expression error, scriptSrcRef="
						+ scriptObj.getScriptSrcRef(), t);
			}
		} else {
			ScriptEngine se = getScriptEngine(scriptObj.getScriptLang());

			try {
				retValue = se.eval(scriptObj.getScriptLang(), oBindings);
			} catch (Throwable t) {
				throw new RuntimeException("Eval expression error, scriptSrcRef=" + scriptObj.getScriptSrcRef(), t);
			}
		}

		return retValue;
	}
}
