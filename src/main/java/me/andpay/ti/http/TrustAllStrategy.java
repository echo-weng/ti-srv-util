package me.andpay.ti.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.TrustStrategy;

/**
 * 信任所有策略类。
 * 
 * @author sea.bao
 */
public class TrustAllStrategy implements TrustStrategy {

	public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		return true;
	}

}
