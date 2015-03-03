package me.andpay.ti.http;

import java.security.KeyStore;

/**
 * Http客户端配置类。
 * 
 * @author sea.bao
 */
public class HttpClientConfig {
	/**
	 * 信任所有主机（用于测试环境）
	 */
	private boolean trustAll = false;
	
	/**
	 * 信任存储
	 */
	private KeyStore trustStore;
	
	/**
	 * 客户端证书密钥存储
	 */
	private KeyStore keyStore;
	
	/**
	 * 密钥密码
	 */
	private String keyPassword;

	/**
	 * 请求超时时间（毫秒）
	 */
	private int requestTimeout = 30000;

	/**
	 * 连接超时时间（毫秒）
	 */
	private int connectTimeout = 20000;

	/**
	 * 最大连接数量
	 */
	private int maxConnTotal = 1000;

	/**
	 * 单一主机最大连接数量
	 */
	private int maxConnPerHost = 500;

	public boolean isTrustAll() {
		return trustAll;
	}

	public void setTrustAll(boolean trustAll) {
		this.trustAll = trustAll;
	}

	public KeyStore getTrustStore() {
		return trustStore;
	}

	public void setTrustStore(KeyStore trustStore) {
		this.trustStore = trustStore;
	}

	public KeyStore getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getMaxConnTotal() {
		return maxConnTotal;
	}

	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}

	public int getMaxConnPerHost() {
		return maxConnPerHost;
	}

	public void setMaxConnPerHost(int maxConnPerHost) {
		this.maxConnPerHost = maxConnPerHost;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}
}
