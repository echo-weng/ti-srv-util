package me.andpay.ti.job;

/**
 * 任务执行器接口类
 * 
 * @author alex
 */
public interface JobInvoker<Data> {
	/**
	 * 执行任务
	 * 
	 * @param jobCtx
	 * @param jobData
	 * @param jobResultHandler
	 *            为null表示不需要处理结果
	 * @return
	 */
	void invoke(JobContext jobCtx, JobData<Data> jobData, JobResultHandler<Data> jobResultHandler);
}
