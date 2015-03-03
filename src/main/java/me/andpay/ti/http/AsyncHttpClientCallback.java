package me.andpay.ti.http;

import org.apache.http.HttpResponse;

/**
 * 异步Http客户端回调接口定义类。
 * 
 * @author sea.bao
 */
public interface AsyncHttpClientCallback {
	/**
	 * 远程访问完成。
	 * 
	 * @param response
	 */
	void completed(HttpResponse response);
	
	/**
	 * 异常失败。
	 * 
	 * @param ex
	 */
	void failed(Exception ex);
	
	/**
	 * 请求撤销
	 */
	void cancelled();
}
