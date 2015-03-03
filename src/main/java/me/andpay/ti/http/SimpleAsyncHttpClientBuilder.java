package me.andpay.ti.http;

/**
 * 简单异步Http客户端构建器类。
 * 
 * @author sea.bao
 */
public class SimpleAsyncHttpClientBuilder {
	public SimpleAsyncHttpClient build() {
		return new DefaultSimpleAsyncHttpClient(getDefaultConfig());
	}
	
	public SimpleAsyncHttpClient build(HttpClientConfig config) {
		return new DefaultSimpleAsyncHttpClient(config);
	}
	
	public HttpClientConfig getDefaultConfig() {
		return new HttpClientConfig();
	}
}
