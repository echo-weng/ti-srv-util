package me.andpay.ti.http;

import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * 缺省简单异步Http客户端测试类。
 * 
 * @author sea.bao
 */
@SuppressWarnings("unused")
public class ScratchTest {
	public static class MyProxyClientCallback implements AsyncHttpClientCallback {
		CountDownLatch latch = new CountDownLatch(1);

		String respBody;

		public void completed(HttpResponse response) {
			if (true) {
				latch.countDown();
				throw new RuntimeException("for test.");
			}

			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				try {
					respBody = (entity != null ? EntityUtils.toString(entity) : null);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}

			latch.countDown();
		}

		public void failed(Exception ex) {
			if (true) {
				latch.countDown();
				throw new RuntimeException("for test.");
			}
			
			latch.countDown();
		}

		public void cancelled() {
			latch.countDown();
		}
	}

	public static void main(String[] args) throws Exception {
		// 测试http
//		SimpleHttpServer httpServer = new SimpleHttpServer();
//		httpServer.afterPropertiesSet();

		SimpleAsyncHttpClientBuilder builder = new SimpleAsyncHttpClientBuilder();
		SimpleAsyncHttpClient client = builder.build();

		MyProxyClientCallback callback = new MyProxyClientCallback();
		HttpGet httpGet = new HttpGet("http://127.0.0.1:6062/oqs?name=proxy");
		client.execute(httpGet, callback);

		callback.latch.await();
		
		Thread.sleep(1000);

		callback.latch = new CountDownLatch(1);
		client.execute(httpGet, callback);
		callback.latch.await();

		client.close();
//		httpServer.destroy();
	}

}
