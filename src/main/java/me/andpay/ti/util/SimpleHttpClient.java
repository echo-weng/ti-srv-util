package me.andpay.ti.util;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.ssl.SSLIOSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP客户端实现类
 * 
 * @author alex
 */
public class SimpleHttpClient implements Closeable {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpClient.class);

	/**
	 * 默认字符集
	 */
	private String defaultEncoding = Charsets.UTF_8;

	/**
	 * 默认MIME类型
	 */
	private String defaultMimeType = "text/xml";

	/**
	 * 连接超时(单位秒, 默认30秒)
	 */
	private int connectTimeout = 30;

	/**
	 * 读取超时(单位秒, 默认60秒)
	 */
	private int soTimeout = 60;

	/**
	 * 每个host最大的连接数
	 */
	private int maxConnPerRoute = 100;

	/**
	 * 最大连接数
	 */
	private int maxConnTotal = 500;

	/**
	 * 检查HTTP应答码, 如果不为200就抛出异常
	 */
	private boolean checkHttpStatusCode = true;

	/**
	 * SSL信赖所有服务端证书
	 */
	private boolean sslTrustAll = true;

	/**
	 * 协议类型
	 */
	private String sslProtocol;

	/**
	 * 密钥库
	 */
	private KeyStore keyStore;

	/**
	 * 密钥库密码
	 */
	private String keyStorePwd;

	/**
	 * 信任密钥库
	 */
	private KeyStore trustStore;

	/**
	 * HTTP客户端
	 */
	private volatile CloseableHttpClient httpClient;

	/**
	 * HTTP异步客户端
	 */
	private volatile CloseableHttpAsyncClient httpAsyncClient;

	/**
	 * 线程池
	 */
	private volatile ThreadPoolExecutor threadPool;

	/**
	 * 执行GET方法
	 * 
	 * @param url
	 * @return
	 */
	public String doGet(String url) {
		return doGet(url, null);
	}

	/**
	 * 通过GET方式访问指定URL并返回应答
	 * 
	 * @param url
	 * @param encoding
	 * @return
	 */
	public String doGet(String url, String encoding) {
		HttpReq req = new HttpReq();
		req.url = url;
		req.encoding = encoding;

		HttpResp resp = new HttpResp();
		resp.encoding = encoding;

		doGet(req, resp);

		return resp.content;
	}

	/**
	 * 通过POST方式将数据发送至指定URL并返回应答
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public String doPost(String url, String content) {
		return doPost(url, content, null, null);
	}

	/**
	 * 通过POST方式将数据发送至指定URL并返回应答
	 * 
	 * @param url
	 * @param content
	 * @param mineType
	 * @return
	 */
	public String doPost(String url, String content, String mimeType) {
		return doPost(url, content, mimeType, null);
	}

	/**
	 * 通过POST方式将数据发送至指定URL并返回应答
	 * 
	 * @param url
	 * @param content
	 * @param mimeType
	 * @param encoding
	 * @return
	 */
	public String doPost(String url, String content, String mimeType, String encoding) {
		HttpReq req = new HttpReq();
		req.url = url;
		req.content = content;
		req.mimeType = mimeType;
		req.encoding = encoding;

		HttpResp resp = new HttpResp();
		resp.encoding = encoding;

		doPost(req, resp);

		return resp.content;
	}

	/**
	 * 通过POST方式将参数集发送至指定URL并返回应答
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String doPost(String url, List<NameValuePair> params) {
		return doPost(url, params, null);
	}

	/**
	 * 通过POST方式将参数集发送至指定URL并返回应答
	 * 
	 * @param url
	 * @param params
	 * @param encoding
	 * @return
	 */
	public String doPost(String url, List<NameValuePair> params, String encoding) {
		HttpReq req = new HttpReq();
		req.url = url;
		req.params = params;
		req.encoding = encoding;

		HttpResp resp = new HttpResp();
		resp.encoding = encoding;

		doPost(req, resp);

		return resp.content;
	}

	/**
	 * 通过GET方式访问指定URL，并将返回结果保存到临时文件中
	 * 
	 * @param url
	 */
	public File downloadFile(String url) {
		File tempFile = FileUtil.createTempFile();
		downloadFile(url, tempFile);

		return tempFile;
	}

	/**
	 * 通过GET方式访问指定URL，并将返回结果保存到指定文件中
	 * 
	 * @param url
	 * @param file
	 * @return
	 */
	public void downloadFile(String url, File file) {
		HttpReq req = new HttpReq();
		req.url = url;

		HttpResp resp = new HttpResp();
		resp.downloadFile = file;

		doGet(req, resp);
	}

	/**
	 * 通过POST方式将文件上传至指定URL，并将返回应答
	 * 
	 * @param url
	 * @param files
	 * @return
	 */
	public String uploadFile(String url, Collection<File> files) {
		return uploadFile(url, files, null);
	}

	/**
	 * 通过POST方式将文件上传至指定URL，并将返回应答
	 * 
	 * @param url
	 * @param files
	 * @param encoding
	 * @return
	 */
	public String uploadFile(String url, Collection<File> files, String encoding) {
		HttpReq req = new HttpReq();
		req.url = url;
		req.encoding = encoding;
		req.setUploadFiles(files);

		HttpResp resp = new HttpResp();
		resp.encoding = encoding;

		doPost(req, resp);

		return resp.content;
	}

	/**
	 * 通过GET方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @return
	 */
	public HttpResp doGet(HttpReq req) {
		HttpResp resp = new HttpResp();
		doGet(req, resp);

		return resp;
	}

	/**
	 * 通过GET方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	public void doGet(HttpReq req, HttpResp resp) {
		execute(req, resp, buildGetRequest(req));
	}

	/**
	 * 通过GET方式异步访问指定URL并返回应答
	 * 
	 * @param req
	 * @param respHandler
	 * @return
	 */
	public Future<HttpResp> doAsyncGet(HttpReq req, HttpAsyncRespHandler respHandler) {
		return asyncExecute(req, new HttpResp(), respHandler, buildGetRequest(req));
	}

	/**
	 * 通过GET方式异步访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 * @param respHandler
	 * @return
	 */
	public Future<HttpResp> doAsyncGet(HttpReq req, HttpResp resp, HttpAsyncRespHandler respHandler) {
		return asyncExecute(req, resp, respHandler, buildGetRequest(req));
	}

	/**
	 * 通过POST方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @return
	 */
	public HttpResp doPost(HttpReq req) {
		HttpResp resp = new HttpResp();
		doPost(req, resp);

		return resp;
	}

	/**
	 * 通过POST方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 */
	public void doPost(HttpReq req, HttpResp resp) {
		execute(req, resp, buildPostRequest(req));
	}

	/**
	 * 通过Post方式异步访问指定URL并返回应答
	 * 
	 * @param req
	 * @param respHandler
	 * @return
	 */
	public Future<HttpResp> doAsyncPost(HttpReq req, HttpAsyncRespHandler respHandler) {
		return asyncExecute(req, new HttpResp(), respHandler, buildPostRequest(req));
	}

	/**
	 * 通过Post方式异步访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 * @param respHandler
	 * @return
	 */
	public Future<HttpResp> doAsyncPost(HttpReq req, HttpResp resp, HttpAsyncRespHandler respHandler) {
		return asyncExecute(req, resp, respHandler, buildPostRequest(req));
	}

	/**
	 * 重置客户端
	 */
	public void reset() {
		synchronized (this) {
			if (threadPool != null) {
				threadPool.shutdownNow();
				threadPool = null;
			}

			IOUtil.closeQuietly(httpClient);
			httpClient = null;

			IOUtil.closeQuietly(httpAsyncClient);
			httpAsyncClient = null;
		}
	}

	/**
	 * 关闭客户端
	 */
	public void close() {
		reset();
	}

	/**
	 * 通过指定方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 * @param httpReq
	 * @return
	 */
	protected void execute(HttpReq req, HttpResp resp, HttpRequestBase httpReq) {
		if (resp == null) {
			throw new IllegalArgumentException("HttpResp cannot be null");
		}

		HttpResponse httpResp;
		try {
			httpResp = getHttpClient().execute(httpReq);
		} catch (Exception e) {
			throw new RuntimeException("Exec http req error", e);
		}

		fillHttpResp(req, resp, httpResp);
	}

	/**
	 * 通过指定方式访问指定URL并返回应答
	 * 
	 * @param req
	 * @param resp
	 * @param httpReq
	 * @return
	 */
	protected Future<HttpResp> asyncExecute(final HttpReq req, final HttpResp resp,
			HttpAsyncRespHandler asyncRespHandler, final HttpRequestBase httpReq) {
		if (resp == null) {
			throw new IllegalArgumentException("HttpResp cannot be null");
		}

		// 封装应答处理器
		final HttpAsyncRespHandler innerAsyncRespHandler = getInnerAsyncRespHandler(asyncRespHandler);

		if (CollectionUtil.isNotEmpty(req.getUploadHttpFiles())) {
			// 异步文件上传
			return asyncFileUpload(req, resp, innerAsyncRespHandler, httpReq);
		}

		final BasicFuture<HttpResp> future = new BasicFuture<HttpResp>();

		FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
			public void completed(HttpResponse httpResp) {
				try {
					future.catchRunner(); // for cancel
					fillHttpResp(req, resp, httpResp);

					if (innerAsyncRespHandler != null) {
						innerAsyncRespHandler.onComplete(resp);
					}

					future.completed(resp);
				} catch (Exception ex) {
					failed(ex);
				}
			}

			public void failed(Exception ex) {
				if (innerAsyncRespHandler != null) {
					innerAsyncRespHandler.onError(ex);
				}

				future.failed(ex);
			}

			public void cancelled() {
				failed(new CancellationException("Http request has been cancelled"));
			}
		};

		Future<HttpResponse> task = getHttpAsyncClient().execute(httpReq, callback);
		future.setTask(task);

		return future;
	}

	/**
	 * 异步文件上传
	 * 
	 * @param req
	 * @param resp
	 * @param asyncRespHandler
	 * @param httpReq
	 * @return
	 */
	protected Future<HttpResp> asyncFileUpload(final HttpReq req, final HttpResp resp,
			final HttpAsyncRespHandler asyncRespHandler, final HttpRequestBase httpReq) {
		return getThreadPool().submit(new Callable<HttpResp>() {
			public HttpResp call() throws Exception {
				try {
					execute(req, resp, httpReq);
					if (asyncRespHandler != null) {
						asyncRespHandler.onComplete(resp);
					}

					return resp;
				} catch (Exception ex) {
					if (asyncRespHandler != null) {
						asyncRespHandler.onError(ex);
					}

					throw ex;
				}
			}
		});
	}

	/**
	 * 构建HTTP GET请求
	 * 
	 * @param req
	 * @return
	 */
	protected HttpGet buildGetRequest(HttpReq req) {
		if (req == null) {
			throw new IllegalArgumentException("HttpReq cannot be null");
		}

		HttpGet httpGet = new HttpGet(req.url);

		if (req.headers != null) {
			for (NameValuePair reqHeader : req.headers) {
				httpGet.addHeader(reqHeader.name, reqHeader.value);
			}
		}

		return httpGet;
	}

	/**
	 * 构建HTTP POST请求
	 * 
	 * @param req
	 * @return
	 */
	protected HttpPost buildPostRequest(HttpReq req) {
		if (req == null) {
			throw new IllegalArgumentException("HttpReq cannot be null");
		}

		HttpPost httpPost = new HttpPost(req.url);

		HttpEntity entity = null;

		String reqEncoding = StringUtil.defaultString(req.encoding, defaultEncoding);
		if (req.uploadFiles != null) {
			MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();

			int i = 0;
			for (HttpFile file : req.uploadFiles) {
				String fileName = (file.fileName != null ? file.fileName : file.file.getName());
				ContentType cotentType = (file.mimeType != null ? ContentType.create(file.mimeType)
						: ContentType.DEFAULT_BINARY);
				try {
					meBuilder.addBinaryBody("file-" + (i++), file.file, cotentType,
							URLEncoder.encode(fileName, Charsets.UTF_8));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}

			if (req.getParams() != null) {
				Charset reqCharset = Charset.forName(reqEncoding);
				for (NameValuePair param : req.getParams()) {
					meBuilder.addTextBody(param.getName(), param.getValue(),
							ContentType.TEXT_PLAIN.withCharset(reqCharset));
				}
			}

			entity = meBuilder.build();

		} else if (req.params != null) {
			List<BasicNameValuePair> nvPairs = new ArrayList<BasicNameValuePair>();
			for (NameValuePair param : req.params) {
				nvPairs.add(new BasicNameValuePair(param.name, param.value));
			}

			try {
				entity = new UrlEncodedFormEntity(nvPairs, reqEncoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}

		} else if (req.content != null) {
			entity = new StringEntity(req.content, ContentType.create(
					StringUtil.defaultString(req.mimeType, defaultMimeType), reqEncoding));
		}

		httpPost.setEntity(entity);

		if (req.headers != null) {
			for (NameValuePair reqHeader : req.headers) {
				httpPost.addHeader(reqHeader.name, reqHeader.value);
			}
		}

		return httpPost;
	}

	/**
	 * 根据应答填充HttpResp对象
	 * 
	 * @param req
	 * @param resp
	 * @param httpResp
	 */
	protected void fillHttpResp(HttpReq req, HttpResp resp, HttpResponse httpResp) {
		try {
			StatusLine respStatus = httpResp.getStatusLine();
			resp.statusCode = respStatus.getStatusCode();
			resp.reasonPhrase = respStatus.getReasonPhrase();
			resp.encoding = getHttpRespEncoding(req, resp, httpResp);

			if (httpResp.getAllHeaders() != null) {
				for (Header respHeader : httpResp.getAllHeaders()) {
					resp.addHeader(respHeader.getName(), respHeader.getValue());
				}
			}
			
			if (httpResp.getEntity() != null) {
				InputStream in = new BufferedHttpEntity(httpResp.getEntity()).getContent();
				if (resp.downloadFile != null) {
					IOUtil.copy(in, resp.downloadFile);

				} else {
					resp.content = IOUtil.toString(in, resp.encoding);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Parse http resp error", e);

		} finally {
			if (resp instanceof CloseableHttpResponse) {
				IOUtil.closeQuietly((CloseableHttpResponse) resp);
			}
		}

		checkHttpRespIfNecessary(resp);
	}

	/**
	 * 检查HTTP应答，状态码不为200抛出错误
	 * 
	 * @param resp
	 */
	private void checkHttpRespIfNecessary(HttpResp resp) {
		if (checkHttpStatusCode && HttpStatus.SC_OK != resp.statusCode) {
			StringBuilder errMsg = new StringBuilder();
			errMsg.append("Invalid http resp statusCode, status=[").append(resp.statusCode);
			errMsg.append("/").append(resp.reasonPhrase).append("]");
			errMsg.append(", content=[").append(resp.content).append("]");

			throw new IllegalStateException(errMsg.toString());
		}
	}

	/**
	 * 获取HTTP应答的字符集
	 * 
	 * @param req
	 * @param resp
	 * @param httpResp
	 * @return
	 */
	private String getHttpRespEncoding(HttpReq req, HttpResp resp, HttpResponse httpResp) {
		if (resp.encoding != null) {
			return resp.encoding;
		}

		HttpEntity entity = httpResp.getEntity();
		if (entity != null) {
			Header contentEncoding = entity.getContentEncoding();
			if (contentEncoding != null && contentEncoding.getValue() != null) {
				return contentEncoding.getValue();
			}
		}

		if (req.encoding != null) {
			return req.encoding;
		}

		return defaultEncoding;
	}

	/**
	 * 获得内部使用的异步应答处理器
	 * 
	 * @param respHandler
	 * @return
	 */
	protected HttpAsyncRespHandler getInnerAsyncRespHandler(HttpAsyncRespHandler respHandler) {
		if (respHandler != null) {
			respHandler = new NoExceptionHttpAsyncRespHandler(respHandler);
		}

		return respHandler;
	}

	/**
	 * 获取HttpClient实例
	 * 
	 * @return
	 */
	protected CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			synchronized (this) {
				if (httpClient == null) {
					try {
						HttpClientBuilder builder = HttpClientBuilder.create();

						// Pool
						builder.setMaxConnTotal(maxConnTotal);
						builder.setMaxConnPerRoute(maxConnPerRoute);

						// Conn
						ConnectionConfig.Builder connBuilder = ConnectionConfig.custom();
						connBuilder.setCharset(Charset.forName(defaultEncoding));
						builder.setDefaultConnectionConfig(connBuilder.build());

						// Close conn after http resp, resp.close() is no use.
						builder.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);

						// Socket
						SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(soTimeout * 1000).build();
						builder.setDefaultSocketConfig(socketConfig);

						RequestConfig.Builder reqCfgBuilder = RequestConfig.custom();
						reqCfgBuilder.setSocketTimeout(soTimeout * 1000);
						reqCfgBuilder.setConnectTimeout(connectTimeout * 1000);
						reqCfgBuilder.setConnectionRequestTimeout(connectTimeout * 1000);
						reqCfgBuilder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
						builder.setDefaultRequestConfig(reqCfgBuilder.build());

						// SSL
						SSLContextBuilder sslCtxBuilder = new SSLContextBuilder();
						sslCtxBuilder.useProtocol(sslProtocol);
						sslCtxBuilder.loadKeyMaterial(keyStore, keyStorePwd != null ? keyStorePwd.toCharArray() : null);
						sslCtxBuilder.loadTrustMaterial(trustStore, sslTrustAll ? new EasyTrustStrategy() : null);

						builder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslCtxBuilder.build(),
								sslTrustAll ? SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
										: new BrowserCompatHostnameVerifier()));
						
						httpClient = builder.build();
					} catch (Exception e) {
						throw new RuntimeException("Init httpClient error", e);
					}
				}
			}
		}

		return httpClient;
	}

	/**
	 * 获取HttpAsyncClient实例
	 * 
	 * @return
	 */
	protected CloseableHttpAsyncClient getHttpAsyncClient() {
		if (httpAsyncClient == null) {
			synchronized (this) {
				if (httpAsyncClient == null) {
					try {
						HttpAsyncClientBuilder builder = HttpAsyncClients.custom();

						// Conn
						builder.setMaxConnPerRoute(maxConnPerRoute);
						builder.setMaxConnTotal(maxConnTotal);

						ConnectionConfig.Builder connBuilder = ConnectionConfig.custom();
						connBuilder.setCharset(Charset.forName(defaultEncoding));
						builder.setDefaultConnectionConfig(connBuilder.build());

						// Close conn after http resp, resp.close() is no use.
						builder.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);

						SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(soTimeout * 1000).build();
						builder.setDefaultSocketConfig(socketConfig);

						RequestConfig.Builder reqCfgBuilder = RequestConfig.custom();
						reqCfgBuilder.setSocketTimeout(soTimeout * 1000);
						reqCfgBuilder.setConnectionRequestTimeout(connectTimeout * 1000);
						reqCfgBuilder.setConnectTimeout(connectTimeout * 1000);
						reqCfgBuilder.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
						builder.setDefaultRequestConfig(reqCfgBuilder.build());

						// SSL
						SSLContextBuilder sslCtxBuilder = new SSLContextBuilder();
						sslCtxBuilder.useProtocol(sslProtocol);
						sslCtxBuilder.loadKeyMaterial(keyStore, keyStorePwd != null ? keyStorePwd.toCharArray() : null);
						sslCtxBuilder.loadTrustMaterial(trustStore, sslTrustAll ? new EasyTrustStrategy() : null);

						builder.setSSLIOSessionFactory(new SSLIOSessionFactory(sslCtxBuilder.build(),
								sslTrustAll ? SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
										: new BrowserCompatHostnameVerifier()));

						CloseableHttpAsyncClient httpAsyncClient = builder.build();
						httpAsyncClient.start();

						this.httpAsyncClient = httpAsyncClient;
					} catch (Exception e) {
						throw new RuntimeException("Init httpClient error", e);
					}
				}
			}
		}

		return httpAsyncClient;
	}

	/**
	 * 获得线程池
	 * 
	 * @return
	 */
	protected ThreadPoolExecutor getThreadPool() {
		if (threadPool == null) {
			synchronized (this) {
				if (threadPool == null) {
					threadPool = new ThreadPoolExecutor(0, maxConnTotal, 0, TimeUnit.SECONDS,
							new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());
				}
			}
		}

		return threadPool;
	}

	/**
	 * @param defaultEncoding
	 *            the defaultEncoding to set
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * @param defaultMimeType
	 *            the defaultMimeType to set
	 */
	public void setDefaultMimeType(String defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param soTimeout
	 *            the soTimeout to set
	 */
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	/**
	 * @param maxConnPerRoute
	 *            the maxConnPerRoute to set
	 */
	public void setMaxConnPerRoute(int maxConnPerRoute) {
		this.maxConnPerRoute = maxConnPerRoute;
	}

	/**
	 * @param maxConnTotal
	 *            the maxConnTotal to set
	 */
	public void setMaxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
	}

	/**
	 * @param checkHttpStatusCode
	 *            the checkHttpStatusCode to set
	 */
	public void setCheckHttpStatusCode(boolean checkHttpStatusCode) {
		this.checkHttpStatusCode = checkHttpStatusCode;
	}

	/**
	 * @param sslTrustAll
	 *            the sslTrustAll to set
	 */
	public void setSslTrustAll(boolean sslTrustAll) {
		this.sslTrustAll = sslTrustAll;
	}

	/**
	 * @param sslProtocol
	 *            the sslProtocol to set
	 */
	public void setSslProtocol(String sslProtocol) {
		this.sslProtocol = sslProtocol;
	}

	/**
	 * @param keyStore
	 *            the keyStore to set
	 */
	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	/**
	 * @param keyStorePwd
	 *            the keyStorePwd to set
	 */
	public void setKeyStorePwd(String keyStorePwd) {
		this.keyStorePwd = keyStorePwd;
	}

	/**
	 * @param trustStore
	 *            the trustStore to set
	 */
	public void setTrustStore(KeyStore trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * 变量名值对工具类
	 * 
	 * @author sea.bao
	 */
	public static class NameValuePairs {
		private List<NameValuePair> values = new ArrayList<NameValuePair>();

		public void add(String name, String value) {
			values.add(new NameValuePair(name, value));
		}

		public List<NameValuePair> getList() {
			return values;
		}

		public Map<String, String[]> getMap() {
			Map<String, List<String>> m = new HashMap<String, List<String>>();
			for (NameValuePair nvp : values) {
				List<String> list = m.get(nvp.name);
				if (list == null) {
					list = new ArrayList<String>();
					m.put(nvp.name, list);
				}

				list.add(nvp.value);
			}

			Map<String, String[]> m2 = new HashMap<String, String[]>();
			for (Map.Entry<String, List<String>> entry : m.entrySet()) {
				String[] v = ArrayUtil.toArray(entry.getValue(), String.class);
				m2.put(entry.getKey(), v);
			}

			return m2;
		}
	}

	/**
	 * 变量名值对
	 * 
	 * @author alex
	 */
	public static class NameValuePair {
		/**
		 * 变量名
		 */
		private String name;

		/**
		 * 变量值
		 */
		private String value;

		/**
		 * @param name
		 * @param value
		 */
		public NameValuePair(String name, String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append(name).append("=").append(value);

			return buff.toString();
		}
	}

	/**
	 * HTTP请求类
	 * 
	 * @author alex
	 */
	public static class HttpReq {
		/**
		 * 地址
		 */
		private String url;

		/**
		 * 内容
		 */
		private String content;

		/**
		 * 请求编码
		 */
		private String encoding;

		/**
		 * MIME类型
		 */
		private String mimeType;

		/**
		 * HTTP头参数
		 */
		private List<NameValuePair> headers;

		/**
		 * HTTP参数
		 */
		private List<NameValuePair> params;

		/**
		 * 上传文件
		 */
		private List<HttpFile> uploadFiles;

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the encoding
		 */
		public String getEncoding() {
			return encoding;
		}

		/**
		 * @param encoding
		 *            the encoding to set
		 */
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}

		/**
		 * @param mimeType
		 *            the mimeType to set
		 */
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}

		/**
		 * @return the headers
		 */
		public List<NameValuePair> getHeaders() {
			return headers;
		}

		/**
		 * @param headers
		 *            the headers to set
		 */
		public void setHeaders(List<NameValuePair> headers) {
			this.headers = headers;
		}

		/**
		 * 添加HTTP头信息
		 * 
		 * @param name
		 * @param value
		 */
		public void addHeader(String name, String value) {
			if (headers == null) {
				headers = new ArrayList<NameValuePair>();
			}

			headers.add(new NameValuePair(name, value));
		}

		/**
		 * @return the params
		 */
		public List<NameValuePair> getParams() {
			return params;
		}

		/**
		 * @param params
		 *            the params to set
		 */
		public void setParams(List<NameValuePair> params) {
			this.params = params;
		}

		/**
		 * 添加HTTP参数信息
		 * 
		 * @param name
		 * @param value
		 */
		public void addParam(String name, String value) {
			if (params == null) {
				params = new ArrayList<NameValuePair>();
			}

			params.add(new NameValuePair(name, value));
		}

		/**
		 * @return the uploadFiles
		 */
		public Collection<HttpFile> getUploadHttpFiles() {
			return uploadFiles;
		}

		/**
		 * @param uploadFiles
		 *            the uploadFiles to set
		 */
		public void setUploadFiles(Collection<File> uploadFiles) {
			if (uploadFiles != null) {
				for (File file : uploadFiles) {
					addUploadFile(file);
				}
			}
		}

		/**
		 * 添加上传文件
		 * 
		 * @param file
		 */
		public void addUploadFile(File file) {
			addUploadFile(file, null, null);
		}

		/**
		 * 添加上传文件
		 * 
		 * @param file
		 * @param fileName
		 */
		public void addUploadFile(File file, String fileName) {
			addUploadFile(file, fileName, null);
		}

		/**
		 * 添加上传文件
		 * 
		 * @param file
		 * @param fileName
		 * @param mimeType
		 */
		public void addUploadFile(File file, String fileName, String mimeType) {
			HttpFile httpFile = new HttpFile();
			httpFile.file = file;
			httpFile.fileName = fileName;
			httpFile.mimeType = mimeType;

			addUploadHttpFile(httpFile);
		}

		/**
		 * 添加上传文件
		 * 
		 * @param httpFile
		 */
		public void addUploadHttpFile(HttpFile httpFile) {
			if (uploadFiles == null) {
				uploadFiles = new ArrayList<HttpFile>();
			}

			uploadFiles.add(httpFile);
		}

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append("{url=").append(url);
			buff.append(", mimeType=").append(mimeType);
			buff.append(", encoding=").append(encoding);
			buff.append(", content=").append(content);
			buff.append(", headers=").append(headers);
			buff.append(", params=").append(params);
			buff.append(", uploadFiles=").append(uploadFiles).append("}");

			return buff.toString();
		}
	}

	/**
	 * HTTP应答类
	 * 
	 * @author alex
	 */
	public static class HttpResp {
		/**
		 * 状态码
		 */
		private int statusCode;

		/**
		 * 状态码说明
		 */
		private String reasonPhrase;

		/**
		 * 内容
		 */
		private String content;

		/**
		 * 下载文件
		 */
		private File downloadFile;

		/**
		 * 编码
		 */
		private String encoding;

		/**
		 * HTTP头参数
		 */
		private List<NameValuePair> headers;

		/**
		 * @return the statusCode
		 */
		public int getStatusCode() {
			return statusCode;
		}

		/**
		 * @param statusCode
		 *            the statusCode to set
		 */
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		/**
		 * @return the reasonPhrase
		 */
		public String getReasonPhrase() {
			return reasonPhrase;
		}

		/**
		 * @param reasonPhrase
		 *            the reasonPhrase to set
		 */
		public void setReasonPhrase(String reasonPhrase) {
			this.reasonPhrase = reasonPhrase;
		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the encoding
		 */
		public String getEncoding() {
			return encoding;
		}

		/**
		 * @param encoding
		 *            the encoding to set
		 */
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		/**
		 * @return the headers
		 */
		public List<NameValuePair> getHeaders() {
			return headers;
		}

		/**
		 * @param headers
		 *            the headers to set
		 */
		public void setHeaders(List<NameValuePair> headers) {
			this.headers = headers;
		}

		/**
		 * 添加HTTP头信息
		 * 
		 * @param name
		 * @param value
		 */
		public void addHeader(String name, String value) {
			if (headers == null) {
				headers = new ArrayList<NameValuePair>();
			}

			headers.add(new NameValuePair(name, value));
		}

		/**
		 * 获得指定Header值
		 * 
		 * @param name
		 * @return
		 */
		public String getHeader(String name) {
			if (headers != null && name != null) {
				for (NameValuePair header : headers) {
					if (name.equalsIgnoreCase(header.name)) {
						return header.value;
					}
				}
			}

			return null;
		}

		/**
		 * @return the downloadFile
		 */
		public File getDownloadFile() {
			return downloadFile;
		}

		/**
		 * @param downloadFile
		 *            the downloadFile to set
		 */
		public void setDownloadFile(File downloadFile) {
			this.downloadFile = downloadFile;
		}

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append("{statusCode=").append(statusCode);
			buff.append(", reasonPhrase=").append(reasonPhrase);
			buff.append(", encoding=").append(encoding);
			buff.append(", content=").append(content);
			buff.append(", headers=").append(headers);
			buff.append(", downloadFile=").append(downloadFile).append("}");

			return buff.toString();
		}
	}

	/**
	 * HTTP文件类
	 * 
	 * @author alex
	 */
	public static class HttpFile {
		/**
		 * 文件对象
		 */
		private File file;

		/**
		 * 媒体类型
		 */
		private String mimeType;

		/**
		 * 文件名
		 */
		private String fileName;

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @param file
		 *            the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}

		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}

		/**
		 * @param mimeType
		 *            the mimeType to set
		 */
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @param fileName
		 *            the fileName to set
		 */
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		/**
		 * {@inheritDoc}
		 */
		public String toString() {
			StringBuilder buff = new StringBuilder();
			buff.append("fileName=").append(fileName);
			buff.append(", mimeType=").append(mimeType);
			buff.append(", file=").append(file);

			return buff.toString();
		}
	}

	/**
	 * HTTP异步应答处理接口
	 * 
	 * @author alex
	 */
	public static interface HttpAsyncRespHandler {
		/**
		 * 正常返回
		 * 
		 * @param resp
		 */
		void onComplete(HttpResp resp);

		/**
		 * 出现异常
		 * 
		 * @param ex
		 */
		void onError(Exception ex);
	}

	/**
	 * Http异步应答处理器实现类(拦截处理类异常并输出日志)
	 * 
	 * @author alex
	 */
	class NoExceptionHttpAsyncRespHandler implements HttpAsyncRespHandler {
		/**
		 * 异步应答处理器
		 */
		private HttpAsyncRespHandler respHandler;

		/**
		 * 构造函数
		 * 
		 * @param respHandler
		 */
		NoExceptionHttpAsyncRespHandler(HttpAsyncRespHandler respHandler) {
			this.respHandler = respHandler;
		}

		/**
		 * {@inheritDoc}
		 */
		public void onComplete(HttpResp resp) {
			try {
				respHandler.onComplete(resp);
			} catch (Throwable t) {
				LOG.error("Invoke onComplete error", t);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void onError(Exception ex) {
			try {
				respHandler.onError(ex);
			} catch (Throwable t) {
				LOG.error("Invoke onError error", t);
			}
		}
	}

	/**
	 * 信赖策略 - 信赖所有证书
	 * 
	 * @author alex
	 */
	public static class EasyTrustStrategy implements TrustStrategy {
		public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			return true;
		}
	}
}
