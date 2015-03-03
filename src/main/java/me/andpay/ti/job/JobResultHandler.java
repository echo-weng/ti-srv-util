package me.andpay.ti.job;

/**
 * 任务结果处理接口类
 * 
 * @author alex
 */
public interface JobResultHandler<Data> {
	/**
	 * 任务执行完毕
	 * 
	 * @param jobResult
	 */
	void onComplete(JobResult<Data> jobResult);
}
