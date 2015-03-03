package me.andpay.ti.util;

import java.io.File;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.andpay.ti.util.SimpleHttpClient.HttpAsyncRespHandler;
import me.andpay.ti.util.SimpleHttpClient.HttpReq;
import me.andpay.ti.util.SimpleHttpClient.HttpResp;
import me.andpay.ti.util.SimpleHttpClient.NameValuePair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * SimpleHTTPClient测试类
 * 
 * @author alex
 */
public class SimpleHttpClientIntegrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpClientIntegrationTest.class);

	private static SimpleHttpClient httpClient = new SimpleHttpClient();
	static {
		httpClient.setCheckHttpStatusCode(false);
	}

	public static void main(String[] args) throws Exception {
		try {
			// testDoGet();
			// testDoPost();
			// testUploadAndDownloadFile();
			 testDoAsyncGet();
			// testDoAsyncPost();
			// testAsyncUploadAndDownloadFile();
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			SleepUtil.sleep(10000L);

			System.exit(0);
		}
	}

	public static void testDoGet() throws Exception {
		// 1
		LOG.info(httpClient.doGet("http://www.andpay.me"));
		LOG.info(httpClient.doGet("http://www.andpay.me", Charsets.GBK)); // 乱码
		LOG.info(httpClient.doGet("https://192.168.1.111:8445/p/a/AAzncKl3Snz3"));

		// 2 - untrusted host
		httpClient.reset();
		httpClient.setSslTrustAll(false);
		try {
			httpClient.doGet("https://192.168.1.111:8445/p/a/AAzncKl3Snz3");
			LOG.error("Test failed, cannot reach here");
		} catch (Exception ex) {
			LOG.info("Expected error, errMsg={}", ex.getMessage());
		}

		// 3
		HttpReq req = new HttpReq();
		req.setUrl("http://192.168.1.111:9099/test/index.jsp");
		req.addHeader("h1", "test");

		HttpResp resp = new HttpResp();
		httpClient.doGet(req, resp);
		LOG.info("RESP_STATUS {}/{}", resp.getStatusCode(), resp.getReasonPhrase());
		for (NameValuePair respHeader : resp.getHeaders()) {
			LOG.info("RESP_HEADER {}={}", respHeader.getName(), respHeader.getValue());
		}
		LOG.info("RESP_CONTENT {}", resp.getContent());

		try {
			httpClient.doGet("https://mch.tenpay.com/cgi-bin/cep_i_query.cgi", Charsets.GBK); // no-cli-cert
		} catch (Exception ex) {
			LOG.info("Expected error, errMsg={}", ex.getMessage());
		}
	}

	public static void testDoPost() throws Exception {
		// 1
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new NameValuePair("url", "www.andpay.me"));
		LOG.info(httpClient.doPost("http://dwz.cn/create.php", nvps));

		// 2
		HttpReq req = new HttpReq();
		req.setUrl("http://192.168.1.111:9099/test/index.jsp");
		req.addHeader("h1", "test");
		req.addParam("p1", "test");

		HttpResp resp = new HttpResp();
		httpClient.doPost(req, resp);
		LOG.info("RESP_STATUS {}/{}", resp.getStatusCode(), resp.getReasonPhrase());
		for (NameValuePair respHeader : resp.getHeaders()) {
			LOG.info("RESP_HEADER {}={}", respHeader.getName(), respHeader.getValue());
		}
		LOG.info("RESP_CONTENT {}", resp.getContent());

		// 3 - SSL with client cert
		httpClient.reset();
		httpClient.setSslTrustAll(false);
		String pwd = "1211332401";
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(ResourceUtils.getURL("classpath:test-data/1211332401.pfx").openStream(), pwd.toCharArray());

		httpClient.setKeyStore(ks);
		httpClient.setKeyStorePwd(pwd);

		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(ResourceUtils.getURL("classpath:test-data/CFT.jks").openStream(), "123456".toCharArray());
		httpClient.setTrustStore(ts);

		// may failed when cert is expired
		LOG.info("resp={}", httpClient.doPost("https://mch.tenpay.com/cgi-bin/cep_i_query.cgi", "<root />", "text/xml",
				Charsets.GBK));
	}

	public static void testUploadAndDownloadFile() {
		File file = new File("target/baidu.gif");
		HttpReq req = new HttpReq();
		req.setUrl("http://192.168.1.111:9090/internal-blob?fileCount=1&type=0.1");
		req.addUploadFile(new File("target/baidu-dl.gif"), "测试の文件123.jpg", "image/gif");

		HttpResp resp = httpClient.doPost(req);

		LOG.info(resp.getContent());

		file = httpClient.downloadFile("http://192.168.1.111:9090/internal-blob?type=0.0&" + resp);
		file.renameTo(new File("target/baidu-dl.gif"));
	}

	public static void testDoAsyncGet() throws Exception {
		// 1 async get
		HttpReq req = new HttpReq();
		req.setUrl("https://stage0:19099/test/index.jsp");

		final Long[] sleepTime = new Long[1];

		HttpAsyncRespHandler respHandler = new HttpAsyncRespHandler() {
			public void onComplete(HttpResp resp) {
				LOG.info("Receive resp, sleepTime={}", sleepTime[0]);
				if (sleepTime[0] != null) {
					SleepUtil.sleep(sleepTime[0]);
					LOG.info("Wake up");
				}
			}

			public void onError(Exception ex) {
				LOG.error("Do async get error", ex);
			}
		};

		Future<HttpResp> future = httpClient.doAsyncGet(req, respHandler);
		LOG.info("Async get result={}", future.get());
		
		// 2 cancel
		sleepTime[0] = 1000L;

		future = httpClient.doAsyncGet(req, respHandler);
		try {
			LOG.info("Async get result={}", future.get(1, TimeUnit.SECONDS));
		} catch (TimeoutException ex) {
			ex.printStackTrace();
		}

		future = httpClient.doAsyncGet(req, respHandler);
		SleepUtil.sleep(10L); // cancel httpreq
		LOG.info("Aysnc get has been cancelled, success={}", future.cancel(true));
		
		future = httpClient.doAsyncGet(req, respHandler);
		SleepUtil.sleep(500L); // cancel respHandler.sleep
		LOG.info("Aysnc get has been cancelled, success={}", future.cancel(true));
	}

	public static void testDoAsyncPost() throws Exception {
		HttpReq req = new HttpReq();
		req.setUrl("https://stage0:19099/test/index.jsp");
		req.addParam("abc", "123");
		req.addParam("测试", "你好");

		Future<HttpResp> future = httpClient.doAsyncPost(req, null);
		LOG.info("Async post resp={}", future.get());

		httpClient.reset();
		httpClient.setSslTrustAll(false);
		String pwd = "1211332401";
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(ResourceUtils.getURL("classpath:test-data/1211332401.pfx").openStream(), pwd.toCharArray());

		httpClient.setKeyStore(ks);
		httpClient.setKeyStorePwd(pwd);

		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(ResourceUtils.getURL("classpath:test-data/CFT.jks").openStream(), "123456".toCharArray());
		httpClient.setTrustStore(ts);

		req = new HttpReq();
		req.setUrl("https://mch.tenpay.com/cgi-bin/cep_i_query.cgi");
		req.setContent("<root />");
		req.setEncoding(Charsets.GBK);

		// may failed when cert is expired
		future = httpClient.doAsyncPost(req, new HttpAsyncRespHandler() {
			public void onComplete(HttpResp resp) {
				LOG.info("Do async ssl post successful, resp={}", resp);
				throw new RuntimeException("Swallowed exception");
			}

			public void onError(Exception ex) {
				LOG.error("Do async ssl post error", ex);
			}
		});
		LOG.info("Async ssl post, resp={}", future.get());
	}

	public static void testAsyncUploadAndDownloadFile() throws Exception {
		HttpReq req = new HttpReq();
		req.setUrl("http://www.baidu.com/img/baidu_sylogo1.gif");

		File file = new File("target/baidu.gif");
		HttpResp resp = new HttpResp();
		resp.setDownloadFile(file);

		HttpAsyncRespHandler respHandler = new HttpAsyncRespHandler() {
			public void onComplete(HttpResp resp) {
				LOG.info("Do async download successful, downloadFileLength={}", resp.getDownloadFile().length());
			}

			public void onError(Exception ex) {
				LOG.error("Do async download error", ex);
			}
		};

		httpClient.doAsyncGet(req, resp, respHandler).get();

		req = new HttpReq();
		req.setUrl("http://192.168.1.111:9090/internal-blob?fileCount=1&type=0.0");
		req.addUploadFile(file, "异步测试の文件.jpg", "image/gif"); // diff

		Future<HttpResp> future = httpClient.doAsyncPost(req, null);
		String ids = future.get().getContent();
		LOG.info("Upload result={}", ids);

		req = new HttpReq();
		req.setUrl("http://192.168.1.111:9090/internal-blob?type=0.0&" + ids);

		resp = new HttpResp();
		resp.setDownloadFile(new File("target/baidu-dl.gif"));

		httpClient.doAsyncGet(req, resp, respHandler);
	}
}
