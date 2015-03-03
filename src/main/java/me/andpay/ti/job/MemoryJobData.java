package me.andpay.ti.job;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于内存的任务数据实现类
 * 
 * @author alex
 */
public class MemoryJobData<Data> implements JobData<Data> {
	/**
	 * 当前记录下标
	 */
	private AtomicInteger idx = new AtomicInteger();

	/**
	 * 数据集合
	 */
	private List<Data> data;

	/**
	 * 构造函数
	 * 
	 * @param data
	 */
	public MemoryJobData(List<Data> data) {
		this.data = data;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Data> next(JobContext jobCtx, int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be large than zero, size=" + size);
		}

		if (data == null) {
			return Collections.emptyList();
		}

		int curIdx = idx.getAndAdd(size);
		if (curIdx < 0 || curIdx >= data.size()) {
			// 无可读数据
			return Collections.emptyList();
		}

		return data.subList(curIdx, Math.min(data.size(), curIdx + size));
	}
}
