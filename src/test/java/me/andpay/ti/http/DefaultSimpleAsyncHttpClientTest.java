package me.andpay.ti.http;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * 缺省简单异步Http客户端测试类。
 * 
 * @author sea.bao
 */
public class DefaultSimpleAsyncHttpClientTest {
	public static class MyProxyClientCallback implements AsyncHttpClientCallback {
		CountDownLatch latch = new CountDownLatch(1);
		
		String respBody;
		
		public void completed(HttpResponse response) {
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
			latch.countDown();
		}

		public void cancelled() {
			latch.countDown();
		}
	}
	
	@Test
	public void test() throws Exception {
		// 测试http
		SimpleHttpServer httpServer = new SimpleHttpServer();
		httpServer.afterPropertiesSet();
		
		SimpleAsyncHttpClientBuilder builder = new SimpleAsyncHttpClientBuilder();
		SimpleAsyncHttpClient client = builder.build();
		
		MyProxyClientCallback callback = new MyProxyClientCallback();
		HttpGet httpGet = new HttpGet("http://127.0.0.1:6062/oqs?name=proxy");
		client.execute(httpGet, callback);
		
		callback.latch.await();
		
		System.out.println("respBody=[" + callback.respBody + "].");
		assertEquals("Hello world, proxy", callback.respBody);
		
		client.close();
		httpServer.destroy();
		
		// 测试单向https
		httpServer = new SimpleHttpServer();
		httpServer.setSsl(true);
		httpServer.afterPropertiesSet();
		
		builder = new SimpleAsyncHttpClientBuilder();
		HttpClientConfig cfg = builder.getDefaultConfig();
		cfg.setTrustAll(true);
		client = builder.build(cfg);
		
		callback = new MyProxyClientCallback();
		httpGet = new HttpGet("https://127.0.0.1:6062/oqs?name=proxy");
		client.execute(httpGet, callback);
		
		callback.latch.await();
		
		System.out.println("respBody=[" + callback.respBody + "].");
		assertEquals("Hello world, proxy", callback.respBody);
		
		client.close();
		httpServer.destroy();
		
		// 测试双向https
		httpServer = new SimpleHttpServer();
		httpServer.setSsl(true);
		httpServer.setNeedClientAuth(true);
		httpServer.afterPropertiesSet();
		
		builder = new SimpleAsyncHttpClientBuilder();
		cfg = builder.getDefaultConfig();
		cfg.setTrustAll(true);
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("keystore/0987654321.jks"), "123456".toCharArray());
		cfg.setKeyStore(ks);
		cfg.setKeyPassword("123456");
		client = builder.build(cfg);
		
		callback = new MyProxyClientCallback();
		httpGet = new HttpGet("https://127.0.0.1:6062/oqs?name=proxy");
		client.execute(httpGet, callback);
		
		callback.latch.await();
		
		System.out.println("respBody=[" + callback.respBody + "].");
		assertEquals("Hello world, proxy", callback.respBody);
		
		client.close();
		httpServer.destroy();
	}

}
