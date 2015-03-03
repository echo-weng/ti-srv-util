package me.andpay.ti.job;

import java.util.List;

/**
 * 数据加载器接口类
 * 
 * @author alex
 */
public interface JobDataLoader<Data> {
	/**
	 * 加载数据
	 * 
	 * @param jobCtx
	 * @param firstIndex
	 * @param dataSize
	 * @return
	 */
	List<Data> load(JobContext jobCtx, int firstIndex, int dataSize);
}
