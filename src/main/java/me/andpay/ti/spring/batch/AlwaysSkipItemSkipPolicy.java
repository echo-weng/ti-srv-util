package me.andpay.ti.spring.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * 始终跳过的策略实现类
 * 
 * @author alex
 */
public class AlwaysSkipItemSkipPolicy implements SkipPolicy {
	private static final Logger LOG = LoggerFactory.getLogger(AlwaysSkipItemSkipPolicy.class);

	/**
	 * 输出异常堆栈
	 */
	private boolean printStackTrace = true;

	/**
	 * {@inheritDoc}
	 */
	public boolean shouldSkip(Throwable t, int skipCount) {
		LOG.warn("Skip step error, skipCount={}, errMsg={}", new Object[] { skipCount, t.getMessage(),
				(printStackTrace ? t : null) });

		return true;
	}

	/**
	 * @param printStackTrace
	 *            the printStackTrace to set
	 */
	public void setPrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = printStackTrace;
	}
}