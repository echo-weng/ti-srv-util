package me.andpay.ti.http;

import me.andpay.ti.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 简单Http服务器类。
 * 
 * @author sea.bao
 */
public class SimpleHttpServer implements InitializingBean, DisposableBean {
	private Log logger = LogFactory.getLog(SimpleHttpServer.class);
	
	private Server server;
	
	private boolean ssl;
	
	private boolean needClientAuth;
	
	public void destroy() throws Exception {
		server.stop();
		server = null;
	}

	public void afterPropertiesSet() throws Exception {
		server = new Server();

		Connector[] connectors = new Connector[1];
		int i = 0;
		SelectChannelConnector connector;

		ListenPortConfig listenPortCfg = new ListenPortConfig();
		listenPortCfg.setKeyManagerPassword("123456");
		listenPortCfg.setKeyStorePassword("123456");
		listenPortCfg.setKeyStorePath("keystore/1234567890.jks");
		listenPortCfg.setNeedClientAuth(needClientAuth);
		listenPortCfg.setPort(6062);
		listenPortCfg.setSsl(true);
		listenPortCfg.setTrustStorePassword("123456");
		listenPortCfg.setTrustStorePath("keystore/ca-cert___AndPay.me_2012.jks");
		
		if (ssl) {
			SslContextFactory sslCtxFactory = new SslContextFactory();

			Assert.assertNotNull("keyStorePath", listenPortCfg.getKeyStorePath());
			sslCtxFactory.setKeyStorePath(listenPortCfg.getKeyStorePath());
			if (listenPortCfg.getKeyStorePassword() != null) {
				sslCtxFactory.setKeyStorePassword(listenPortCfg.getKeyStorePassword());
			}

			sslCtxFactory.setTrustStore(listenPortCfg.getTrustStorePath());
			if (listenPortCfg.getTrustStorePassword() != null) {
				sslCtxFactory.setTrustStorePassword(listenPortCfg.getTrustStorePassword());
			}

			sslCtxFactory.setTrustStore(listenPortCfg.getTrustStorePath());
			if (listenPortCfg.getKeyManagerPassword() != null) {
				sslCtxFactory.setKeyManagerPassword(listenPortCfg.getKeyManagerPassword());
			}

			sslCtxFactory.setNeedClientAuth(listenPortCfg.isNeedClientAuth());
			sslCtxFactory.setCertAlias(listenPortCfg.getCertAlias());

			connector = new SslSelectChannelConnector(sslCtxFactory);
		} else {
			connector = new SelectChannelConnector();
		}

		logger.info("HttpServer listen port=[" + listenPortCfg.getPort() + "].");

		connector.setPort(listenPortCfg.getPort());
		connectors[i++] = connector;

		server.setConnectors(connectors);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		HelloworldServlet servlet = new HelloworldServlet();
		context.addServlet(new ServletHolder(servlet), "/*");

		server.start();
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isNeedClientAuth() {
		return needClientAuth;
	}

	public void setNeedClientAuth(boolean needClientAuth) {
		this.needClientAuth = needClientAuth;
	}

}
