package me.andpay.ti.job;

/**
 * 任务执行工作者接口类
 * 
 * @author alex
 */
public interface JobWorker<Data> {
	/**
	 * 执行任务
	 * 
	 * @param jobCtx
	 * @param data
	 * @return 是否成功
	 */
	boolean execute(JobContext jobCtx, Data data);
}
