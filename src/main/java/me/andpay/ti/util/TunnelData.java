package me.andpay.ti.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import me.andpay.ti.util.JSON;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 隧道数据映射工具类
 * 
 * @author alex
 */
public class TunnelData implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 隧道数据
	 */
	private String tunnelData;

	/**
	 * 隧道数据键值对(临时变量)
	 */
	private transient Map<String, Object> tunnelDataMap;

	/**
	 * 构造函数
	 */
	public TunnelData() {
	}

	/**
	 * 构造函数
	 * 
	 * @param tunnelData
	 */
	public TunnelData(String tunnelData) {
		setTunnelData(tunnelData);
	}

	/**
	 * 构造函数
	 * 
	 * @param tunnelDataMap
	 */
	public TunnelData(Map<String, Object> tunnelDataMap) {
		setTunnelDataMap(tunnelDataMap);
	}

	/**
	 * 获取隧道数据键值对
	 * 
	 * @return
	 */
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTunnelDataMap() {
		if (tunnelDataMap == null && tunnelData != null) {
			tunnelDataMap = (Map<String, Object>) JSON.getDefault().parseToObject(tunnelData, Map.class);
		}

		return tunnelDataMap;
	}

	/**
	 * 设置隧道数据键值对
	 * 
	 * @param tunnelDataMap
	 */
	public void setTunnelDataMap(Map<String, Object> tunnelDataMap) {
		this.tunnelDataMap = tunnelDataMap;
		this.tunnelData = JSON.getDefault().toJSONString(tunnelDataMap);
	}

	/**
	 * 获取隧道数据指定标签值
	 * 
	 * @param name
	 * @return
	 */
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public <T> T getTunnelTag(String name) {
		Map<String, Object> tunnelDataMap = getTunnelDataMap();
		if (tunnelDataMap != null) {
			return (T) tunnelDataMap.get(name);
		}

		return null;
	}

	/**
	 * 设置标签值
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T setTunnelTag(String name, Object value) {
		Map<String, Object> tunnelDataMap = getTunnelDataMap();
		if (tunnelDataMap == null) {
			tunnelDataMap = new LinkedHashMap<String, Object>();
		}

		T oldValue = (T) tunnelDataMap.put(name, value);
		setTunnelDataMap(tunnelDataMap);
		return oldValue;
	}

	/**
	 * 移除标签
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeTunnelTag(String name) {
		Map<String, Object> tunnelDataMap = getTunnelDataMap();
		if (tunnelDataMap == null) {
			return null;
		}

		T oldValue = (T) tunnelDataMap.remove(name);
		setTunnelDataMap(tunnelDataMap);
		return oldValue;
	}

	/**
	 * 如果value不为null，则设置标签值；否则，清除对应标签
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public <T> T setOrRemoveTunnelTag(String name, Object value) {
		if (value == null) {
			return removeTunnelTag(name);

		} else {
			return setTunnelTag(name, value);
		}
	}

	/**
	 * 设置标签值如果value不为null
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public <T> T setTunnelTagIfNotNull(String name, Object value) {
		if (value == null) {
			return null;
		}

		return setTunnelTag(name, value);
	}

	/**
	 * 设置标签值如果value不为null
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public <T> T setTunnelTagIfNotEqual(String name, Object value) {
		T curValue = getTunnelTag(name);
		if (curValue == null && value == null) {
			return null;
		}

		if (curValue != null && value != null && curValue.equals(value)) {
			return null;
		}

		return setTunnelTag(name, value);
	}

	/**
	 * 是否包含该标签
	 * 
	 * @param name
	 * @return
	 */
	public boolean containsTag(String name) {
		Map<String, Object> tunnelDataMap = getTunnelDataMap();
		return (tunnelDataMap != null ? tunnelDataMap.containsKey(name) : false);
	}

	/**
	 * 获取隧道数据字符串值
	 * 
	 * @return
	 */
	public String getTunnelData() {
		return tunnelData;
	}

	/**
	 * 设置隧道数据字符串值
	 * 
	 * @param tunnelData
	 */
	public void setTunnelData(String tunnelData) {
		this.tunnelData = tunnelData;
		this.tunnelDataMap = null;
	}
}
