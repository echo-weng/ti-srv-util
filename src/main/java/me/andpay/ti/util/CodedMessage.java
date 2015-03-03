package me.andpay.ti.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 编码方式的消息类。
 * 
 * @author sea.bao
 */
public class CodedMessage {
	/**
	 * 消息代码
	 */
	private String code;
	
	/**
	 * 消息属性集
	 */
	private Map<String, String> properties;

	public static CodedMessage newCodedMessage(String code, String... props) {
		CodedMessage cmsg = new CodedMessage();
		cmsg.setCode(code);
		
		Map<String, String> properties = new HashMap<String, String>();
		for (int i=0; i < props.length; i++) {
			properties.put("a" + i, props[i]);
		}
		
		cmsg.setProperties(properties);
		
		return cmsg;
	}
	
	public static CodedMessage newCodedMessage(String code, Map<String, String> props) {
		CodedMessage cmsg = new CodedMessage();
		cmsg.setCode(code);
		cmsg.setProperties(props);
		
		return cmsg;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
