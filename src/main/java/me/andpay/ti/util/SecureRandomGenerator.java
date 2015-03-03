package me.andpay.ti.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 安全随机数生成器类。
 * 
 * @author sea.bao
 */
public class SecureRandomGenerator {
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 随机器队列
	 */
	private Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<SecureRandom>();

	/**
	 * 安全随机数算法
	 */
	private String secureRandomAlgorithm = "SHA1PRNG";

	/**
	 * 安全随机数提供者
	 */
	private String secureRandomProvider = null;

	/**
	 * 安全随机数类
	 */
	private String secureRandomClass = null;

	/**
	 * Create a new random number generator instance we should use for
	 * generating session identifiers.
	 */
	private SecureRandom createSecureRandom() {
		SecureRandom result = null;

		if (secureRandomClass != null) {
			try {
				// Construct and seed a new random number generator
				Class<?> clazz = Class.forName(secureRandomClass);
				result = (SecureRandom) clazz.newInstance();
			} catch (Exception e) {
				logger.error("Random meet error.", e);
			}
		}

		if (result == null) {
			// No secureRandomClass or creation failed. Use SecureRandom.
			try {
				if (secureRandomProvider != null && secureRandomProvider.length() > 0) {
					result = SecureRandom.getInstance(secureRandomAlgorithm, secureRandomProvider);
				} else if (secureRandomAlgorithm != null && secureRandomAlgorithm.length() > 0) {
					result = SecureRandom.getInstance(secureRandomAlgorithm);
				}
			} catch (NoSuchAlgorithmException e) {
				logger.error("sessionIdGenerator.randomAlgorithm meet error.", e);
			} catch (NoSuchProviderException e) {
				logger.error("sessionIdGenerator.randomProvider meet error.", e);
			}
		}

		if (result == null) {
			// Invalid provider / algorithm
			try {
				result = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				logger.error("sessionIdGenerator.randomAlgorithm meet error.", e);
			}
		}

		if (result == null) {
			// Nothing works - use platform default
			result = new SecureRandom();
		}

		// Force seeding to take place
		result.nextInt();

		return result;
	}

	public byte[] genRandomBytes(int bytes) {
		SecureRandom random = randoms.poll();
		if (random == null) {
			random = createSecureRandom();
		}

		try {
			byte[] data = new byte[bytes];
			random.nextBytes(data);
			return data;
		} finally {
			randoms.add(random);
		}
	}

	public long nextLong() {
		SecureRandom random = randoms.poll();
		if (random == null) {
			random = createSecureRandom();
		}

		try {
			return random.nextLong();
		} finally {
			randoms.add(random);
		}
	}
}
