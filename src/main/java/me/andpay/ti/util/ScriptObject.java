package me.andpay.ti.util;

import javax.script.CompiledScript;

/**
 * 脚本对象类。
 * 
 * @author sea.bao
 */
public class ScriptObject {
	/**
	 * 脚本语言
	 */
	private String scriptLang;
	
	/**
	 * 脚本
	 */
	private String script;
	
	/**
	 * 编译的脚本
	 */
	private CompiledScript compiledScript;

	/**
	 * 脚本源代码参考
	 */
	private String scriptSrcRef;
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

	public void setCompiledScript(CompiledScript compiledScript) {
		this.compiledScript = compiledScript;
	}

	public String getScriptLang() {
		return scriptLang;
	}

	public void setScriptLang(String scriptLang) {
		this.scriptLang = scriptLang;
	}

	public String getScriptSrcRef() {
		return scriptSrcRef;
	}

	public void setScriptSrcRef(String scriptSrcRef) {
		this.scriptSrcRef = scriptSrcRef;
	}
}
