package me.andpay.ti.http;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionFactory;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;

/**
 * 缺省简单异步Http客户端类。
 * 
 * @author sea.bao
 */
public class DefaultSimpleAsyncHttpClient implements SimpleAsyncHttpClient {
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 配置
	 */
	private HttpClientConfig config;

	/**
	 * Http客户端
	 */
	private CloseableHttpAsyncClient httpClient;

	public void build() {
		try {
			SSLContextBuilder builder = new SSLContextBuilder();
			builder = builder.useTLS();

			if (config.isTrustAll()) {
				// 信任所有主机包括任意证书、主机名，用于测试环境
				builder = builder.loadTrustMaterial(null, new TrustAllStrategy());
			} else if (config.getTrustStore() != null) {
				builder = builder.loadTrustMaterial(config.getTrustStore());
			}

			if (config.getKeyStore() != null) {
				char[] password = null;
				if (config.getKeyPassword() != null) {
					password = config.getKeyPassword().toCharArray();
				}

				builder = builder.loadKeyMaterial(config.getKeyStore(), password);
			}

			SSLIOSessionFactory sslFactory;
			if (config.isTrustAll()) {
				sslFactory = new SSLIOSessionFactory(builder.build(), new AllowAllHostnameVerifier());
			} else {
				sslFactory = new SSLIOSessionFactory(builder.build());
			}

			final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(config.getRequestTimeout()).build();
			final RequestConfig requestConfig = RequestConfig.custom().setStaleConnectionCheckEnabled(true)
					.setSocketTimeout(config.getRequestTimeout())
					.setConnectionRequestTimeout(config.getConnectTimeout())
					.setConnectTimeout(config.getConnectTimeout()).build();
			final CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom().setSSLIOSessionFactory(sslFactory)
					.setMaxConnPerRoute(config.getMaxConnPerHost()).setMaxConnTotal(config.getMaxConnTotal())
					.setConnectionReuseStrategy(new NoConnectionReuseStrategy()).setDefaultSocketConfig(socketConfig)
					.setDefaultRequestConfig(requestConfig).build();

			httpClient.start();
			this.httpClient = httpClient;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public DefaultSimpleAsyncHttpClient(HttpClientConfig config) {
		this.config = config;
		build();
	}

	private void restart() {
		try {
			close();
			build();

			logger.info("Restarted asyncHttpClient.");
		} catch (Throwable e) {
			logger.error("AsyncHttpClient restart meet error.", e);
		}
	}

	public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer,
			HttpAsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback) {
		try {
			return httpClient.execute(requestProducer, responseConsumer, callback);
		} catch (RuntimeException e) {
			// 出现运行态异常，重新启动客户端
			restart();
			throw e;
		}
	}

	public Future<HttpResponse> execute(HttpUriRequest httpRequest, final AsyncHttpClientCallback callback) {
		try {
			return httpClient.execute(httpRequest, new FutureCallback<HttpResponse>() {

				public void completed(HttpResponse response) {
					try {
						callback.completed(response);
					} catch (Throwable e) {
						logger.error("AsyncHttpClient completed error.", e);
					}
				}

				public void failed(Exception ex) {
					try {
						callback.failed(ex);
					} catch (Throwable e) {
						logger.error("AsyncHttpClient failed error.", e);
					}
				}

				public void cancelled() {
					try {
						callback.cancelled();
					} catch (Throwable e) {
						logger.error("AsyncHttpClient cancelled error.", e);
					}
				}

			});
		} catch (RuntimeException e) {
			// 出现运行态异常，重新启动客户端
			restart();
			throw e;
		}
	}

	public void close() {
		try {
			if ( httpClient != null ) {
				httpClient.close();
			}
		} catch (IOException e) {
		} finally {
			httpClient = null;
		}
	}

}
