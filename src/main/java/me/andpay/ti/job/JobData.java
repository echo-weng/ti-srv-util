package me.andpay.ti.job;

import java.util.List;

/**
 * 任务数据对象
 * 
 * @author alex
 */
public interface JobData<Data> {
	/**
	 * 尝试返回指定数量的数据，如果数据集的大小小于指定大小，则表示无后续数据
	 * 
	 * @param jobCtx
	 * @param size
	 * @return
	 */
	List<Data> next(JobContext jobCtx, int size);
}
