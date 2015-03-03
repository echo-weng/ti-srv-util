package me.andpay.ti.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * Spring批处理工具类。
 * 
 * @author sea.bao
 */
public class SpringBatchUtil {
	/**
	 * 运行SpringBatch任务
	 * 
	 * @param jobLauncher
	 * @param job
	 * @param jobParas
	 */
	public static JobExecution executeJob(JobLauncher jobLauncher, Job job, JobParameters jobParas, Class<?>... ignoreExClasses) {
		JobExecution jobExe;
		try {
			jobExe = jobLauncher.run(job, jobParas);
		} catch (Exception e) {
			throw new RuntimeException("Run springBatchJob meet error", e);
		}

		handleBatchFailureExceptions(jobExe, ignoreExClasses);
		
		return jobExe;
	}
	
	public static void handleBatchFailureExceptions(JobExecution jobExec, Class<?>... ignoreExClasses) {
		if (jobExec.getAllFailureExceptions() != null) {
			for (Throwable e : jobExec.getAllFailureExceptions()) {
				boolean ignore = false;
				for (Class<?> exClass : ignoreExClasses) {
					if (exClass.isInstance(e)) {
						ignore = true;
						break;
					}
				}

				if (ignore) {
					continue;
				}

				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else {
					throw new RuntimeException("Run springBatchJob meet error, instanceId [" + jobExec.getId() + "]", e);
				}
			}
		}
	}
}
