package me.andpay.ti.job;

/**
 * 任务配置类
 * 
 * @author alex
 */
public class JobConfig {
	/**
	 * 默认配置
	 */
	public static final JobConfig DEFAULT = new JobConfig();

	/**
	 * 异步执行任务(启动新线程执行invoke方法)
	 */
	private boolean asyncInvoking = true;

	/**
	 * 任务超时时间(单位: 秒)，0代表无超时
	 */
	private int jobTimeout = 60 * 60;

	/**
	 * 工作线程数，最小1个线程
	 */
	private int jobWorkerSize = 1;

	/**
	 * 批次记录数
	 */
	private int jobBatchSize = 10;

	/**
	 * 任务间隔时间(单位: 毫秒)，0代表无间隔
	 */
	private long jobInterval = 0L;

	/**
	 * 任务重试次数
	 */
	private int jobRetryLimit = 1;

	/**
	 * 快速失败(只要有一个数据处理失败则停止后续任务，主要针对单线程任务)
	 */
	private boolean fastFail = false;

	/**
	 * 只记录处理结果的成功失败次数
	 */
	private boolean recordCountOnly = true;

	/**
	 * @return the asyncInvoking
	 */
	public boolean isAsyncInvoking() {
		return asyncInvoking;
	}

	/**
	 * @param asyncInvoking
	 *            the asyncInvoking to set
	 */
	public void setAsyncInvoking(boolean asyncInvoking) {
		this.asyncInvoking = asyncInvoking;
	}

	/**
	 * @return the jobTimeout
	 */
	public int getJobTimeout() {
		return jobTimeout;
	}

	/**
	 * @param jobTimeout
	 *            the jobTimeout to set
	 */
	public void setJobTimeout(int jobTimeout) {
		if (jobTimeout < 0) {
			throw new IllegalArgumentException("Job timeout must be large than or equal to zero, jobTimeout="
					+ jobTimeout);
		}

		this.jobTimeout = jobTimeout;
	}

	/**
	 * @return the jobWorkerSize
	 */
	public int getJobWorkerSize() {
		return jobWorkerSize;
	}

	/**
	 * @param jobWorkerSize
	 *            the jobWorkerSize to set
	 */
	public void setJobWorkerSize(int jobWorkerSize) {
		if (jobWorkerSize < 1) {
			throw new IllegalArgumentException("Job worker size must be large than one, jobWorkerSize=" + jobWorkerSize);
		}

		this.jobWorkerSize = jobWorkerSize;
	}

	/**
	 * @return the jobBatchSize
	 */
	public int getJobBatchSize() {
		return jobBatchSize;
	}

	/**
	 * @param jobBatchSize
	 *            the jobBatchSize to set
	 */
	public void setJobBatchSize(int jobBatchSize) {
		if (jobBatchSize < 1) {
			throw new IllegalArgumentException("Job batch size must be large than one, jobBatchSize=" + jobBatchSize);
		}

		this.jobBatchSize = jobBatchSize;
	}

	/**
	 * @return the jobInterval
	 */
	public long getJobInterval() {
		return jobInterval;
	}

	/**
	 * @param jobInterval
	 *            the jobInterval to set
	 */
	public void setJobInterval(long jobInterval) {
		if (jobInterval < 0) {
			throw new IllegalArgumentException("Job interval must be large than or equal to zero, jobInterval="
					+ jobInterval);
		}
		this.jobInterval = jobInterval;
	}

	/**
	 * @return the fastFail
	 */
	public boolean isFastFail() {
		return fastFail;
	}

	/**
	 * @param fastFail
	 *            the fastFail to set
	 */
	public void setFastFail(boolean fastFail) {
		this.fastFail = fastFail;
	}

	/**
	 * @return the recordCountOnly
	 */
	public boolean isRecordCountOnly() {
		return recordCountOnly;
	}

	/**
	 * @param recordCountOnly
	 *            the recordCountOnly to set
	 */
	public void setRecordCountOnly(boolean recordCountOnly) {
		this.recordCountOnly = recordCountOnly;
	}

	/**
	 * @return the jobRetryLimit
	 */
	public int getJobRetryLimit() {
		return jobRetryLimit;
	}

	/**
	 * @param jobRetryLimit
	 *            the jobRetryLimit to set
	 */
	public void setJobRetryLimit(int jobRetryLimit) {
		if (jobRetryLimit < 1) {
			throw new IllegalArgumentException("Job retry limit must be large than zero, jobRetryLimit="
					+ jobRetryLimit);
		}
		this.jobRetryLimit = jobRetryLimit;
	}
}
