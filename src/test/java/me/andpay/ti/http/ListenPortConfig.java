package me.andpay.ti.http;

/**
 * 监听端口配置类。
 * 
 * @author sea.bao
 */
public class ListenPortConfig {
	/**
	 * 监听端口
	 */
	private int port;
	
	/**
	 * ssl标志
	 */
	private boolean ssl;

	/**
	 * 信任存储路径
	 */
	private String trustStorePath;

	/**
	 * 信任存储密码
	 */
	private String trustStorePassword;

	/**
	 * 密钥存储路径
	 */
	private String keyStorePath;

	/**
	 * 密钥存储密码
	 */
	private String keyStorePassword;
	
	/**
	 * 密码管理器密码
	 */
	private String keyManagerPassword;
	
	/**
	 * 证书别名
	 */
	private String certAlias;
	
	/**
	 * 需要客户端认证（双向认证）
	 */
	private boolean needClientAuth;

	public ListenPortConfig(int port) {
		this.port = port;
		this.ssl = false;
	}
	
	public ListenPortConfig() {
	}
	
	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public boolean isNeedClientAuth() {
		return needClientAuth;
	}

	public void setNeedClientAuth(boolean needClientAuth) {
		this.needClientAuth = needClientAuth;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getKeyManagerPassword() {
		return keyManagerPassword;
	}

	public void setKeyManagerPassword(String keyManagerPassword) {
		this.keyManagerPassword = keyManagerPassword;
	}
	
}
