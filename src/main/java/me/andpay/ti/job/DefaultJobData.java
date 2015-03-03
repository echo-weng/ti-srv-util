package me.andpay.ti.job;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认任务数据实现类
 * 
 * @author alex
 */
public class DefaultJobData<Data> implements JobData<Data> {
	/**
	 * 当前记录下标
	 */
	private AtomicInteger idx = new AtomicInteger();

	/**
	 * 数据加载器
	 */
	private JobDataLoader<Data> jobDataLoader;

	/**
	 * 构造函数
	 * 
	 * @param jobDataLoader
	 */
	public DefaultJobData(JobDataLoader<Data> jobDataLoader) {
		this.jobDataLoader = jobDataLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Data> next(JobContext jobCtx, int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be large than zero, size=" + size);
		}

		int curIdx = idx.getAndAdd(size);
		if (curIdx < 0) {
			return Collections.emptyList();
		}

		List<Data> dataList = jobDataLoader.load(jobCtx, curIdx, size);
		if (dataList == null) {
			dataList = Collections.emptyList();
		}

		return dataList;
	}
}
