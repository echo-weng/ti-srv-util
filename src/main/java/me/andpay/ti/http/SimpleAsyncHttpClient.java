package me.andpay.ti.http;

import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;

/**
 * 简单异步Http客户端接口定义类。
 * 
 * @author sea.bao
 */
public interface SimpleAsyncHttpClient {
	/**
	 * 执行异步访问。
	 * 
	 * @param httpRequest
	 * @param callback
	 * @return
	 */
	Future<HttpResponse> execute(HttpUriRequest httpRequest, AsyncHttpClientCallback callback);

	/**
	 * 执行异步访问
	 * 
	 * @param requestProducer
	 * @param responseConsumer
	 * @param callback
	 * @return
	 */
	<T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer,
			FutureCallback<T> callback);

	/**
	 * 关闭
	 */
	void close();
}
