package me.andpay.ti.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.andpay.ti.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于线程池的多线程任务执行器实现类
 * 
 * @author alex
 */
public class MultiThreadJobInvoker<Data> implements JobInvoker<Data> {
	private static final Logger LOG = LoggerFactory.getLogger(MultiThreadJobInvoker.class);

	/**
	 * 任务配置
	 */
	private JobConfig jobConfig = JobConfig.DEFAULT;

	/**
	 * 任务处理器
	 */
	private JobWorker<Data> jobWorker;

	/**
	 * {@inheritDoc}
	 */
	public void invoke(final JobContext jobCtx, final JobData<Data> jobData,
			final JobResultHandler<Data> jobResultHandler) {
		if (jobWorker == null) {
			throw new IllegalStateException("JobWorker cannot be null");

		} else if (jobCtx == null) {
			throw new IllegalArgumentException("JobContext cannot be null");

		} else if (jobData == null) {
			throw new IllegalArgumentException("JobData cannot be null");
		}

		if (jobConfig.isAsyncInvoking()) {
			// 异步执行
			Thread asyncInvokeThread = new Thread() {
				public void run() {
					JobResult<Data> jobResult = doInvoke(jobCtx, jobData);
					if (jobResultHandler != null) {
						jobResultHandler.onComplete(jobResult);
					}
				}
			};
			asyncInvokeThread.setDaemon(true);
			asyncInvokeThread.start();

		} else {
			// 同步执行
			JobResult<Data> jobResult = doInvoke(jobCtx, jobData);
			if (jobResultHandler != null) {
				jobResultHandler.onComplete(jobResult);
			}
		}
	}

	/**
	 * 执行任务
	 * 
	 * @param jobCtx
	 * @param jobData
	 * @return
	 */
	private JobResult<Data> doInvoke(JobContext jobCtx, JobData<Data> jobData) {
		LOG.info(
				"Job start, jobName={}, jobWorkerSize={}, jobTimeout={}s, jobInterval={}, jobBatchSize={}, jobRetryLimit={}",
				new Object[] { jobCtx.getJobName(), jobConfig.getJobWorkerSize(), jobConfig.getJobTimeout(),
						jobConfig.getJobInterval(), jobConfig.getJobBatchSize(), jobConfig.getJobRetryLimit() });

		ThreadPoolExecutor executor = initExecutor(jobConfig);
		JobResult<Data> jobResult = new JobResult<Data>(jobConfig.isRecordCountOnly());

		try {
			doInvokeImpl(executor, jobCtx, jobData, jobResult);
		} catch (Throwable t) {
			LOG.error("Invoking job error, jobName={}", jobCtx.getJobName(), t);

		} finally {
			// 关闭线程池
			executor.shutdownNow();
		}

		jobCtx.setFinishTime(new Date());

		LOG.info(
				"Job finished, done={}, jobName={}, jobCount={}, taskCount(s/f/t)={}/{}/{}, avgDataLoadElapsed={}ms, avgProcElapsed={}ms",
				new Object[] { jobResult.isDone(), jobCtx.getJobName(), jobResult.getJobCount(),
						jobResult.getSuccCount(), jobResult.getFailCount(), jobResult.getTotalCount(),
						jobResult.getDataLoadElapsedTime() / jobResult.getJobCount(),
						jobResult.getProcElapsedTime() / jobResult.getJobCount() });

		return jobResult;
	}

	/**
	 * 执行任务
	 * 
	 * @param executor
	 * @param jobCtx
	 * @param jobData
	 * @param jobResult
	 * @throws Exception
	 */
	private void doInvokeImpl(ThreadPoolExecutor executor, JobContext jobCtx, JobData<Data> jobData,
			JobResult<Data> jobResult) throws Exception {

		List<SubJob<Data>> subJobs = new ArrayList<SubJob<Data>>();
		for (int i = 0; i < executor.getMaximumPoolSize(); i++) {
			subJobs.add(new SubJob<Data>(jobConfig, jobCtx, jobData, jobWorker));
		}

		try {
			// 执行子任务
			List<Future<JobResult<Data>>> futures = null;

			if (jobConfig.getJobTimeout() == 0) {
				// 无超时
				futures = executor.invokeAll(subJobs);

			} else {
				futures = executor.invokeAll(subJobs, jobConfig.getJobTimeout(), TimeUnit.SECONDS);
			}

			// 检查所有子任务是否已完成
			boolean done = true;
			for (Future<JobResult<Data>> future : futures) {
				done &= (future.isDone() && future.isCancelled() == false);
			}
			jobResult.setDone(done);
		} catch (Throwable t) {
			LOG.error("Invoking sub jobs error, jobName={}", jobCtx.getJobName(), t);
		}

		// 汇总数据
		jobResult.readyForSum();
		for (SubJob<Data> subJob : subJobs) {
			jobResult.summarize(subJob.jobResult);
		}
	}

	/**
	 * 初始化线程池
	 * 
	 * @param jobConfig
	 * @return
	 */
	private ThreadPoolExecutor initExecutor(JobConfig jobConfig) {
		final SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
		return new ThreadPoolExecutor(1, jobConfig.getJobWorkerSize(), 60L, TimeUnit.SECONDS, queue,
				new RejectedExecutionHandler() {
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						try {
							queue.put(r);
						} catch (Exception e) {
							LOG.error("Task queue up failed", e);
							throw new RejectedExecutionException(e);
						}
					}
				});
	}

	/**
	 * @param jobConfig
	 *            the jobConfig to set
	 */
	public void setJobConfig(JobConfig jobConfig) {
		this.jobConfig = jobConfig;
	}

	/**
	 * @param jobWorker
	 *            the jobWorker to set
	 */
	public void setJobWorker(JobWorker<Data> jobWorker) {
		this.jobWorker = jobWorker;
	}

	/**
	 * 子任务类
	 * 
	 * @author alex
	 */
	static class SubJob<Data> implements Callable<JobResult<Data>> {
		/**
		 * 任务配置
		 */
		private JobConfig jobConfig;

		/**
		 * 任务上下文
		 */
		private JobContext jobCtx;

		/**
		 * 任务数据
		 */
		private JobData<Data> jobData;

		/**
		 * 任务工作器
		 */
		private JobWorker<Data> jobWorker;

		/**
		 * 任务结果
		 */
		private JobResult<Data> jobResult;

		/**
		 * 构造函数
		 * 
		 * @param jobConfig
		 * @param jobCtx
		 * @param jobData
		 * @param jobWorker
		 */
		public SubJob(JobConfig jobConfig, JobContext jobCtx, JobData<Data> jobData, JobWorker<Data> jobWorker) {
			this.jobConfig = jobConfig;
			this.jobCtx = jobCtx;
			this.jobData = jobData;
			this.jobWorker = jobWorker;
			this.jobResult = new JobResult<Data>(jobConfig.isRecordCountOnly());
		}

		/**
		 * {@inheritDoc}
		 */
		public JobResult<Data> call() throws Exception {
			try {
				return callImpl();
			} catch (Exception e) {
				LOG.error(
						"Invoking sub job error, jobName={}, count(s/f/t)={}/{}/{}, dataLoadElapsed={}ms, procElapsed={}ms",
						new Object[] { jobCtx.getJobName(), jobResult.getSuccCount(), jobResult.getFailCount(),
								jobResult.getTotalCount(), jobResult.getDataLoadElapsedTime(),
								jobResult.getProcElapsedTime(), e });
				throw e;
			}
		}

		/**
		 * 执行任务
		 * 
		 * @return
		 * @throws Exception
		 */
		private JobResult<Data> callImpl() throws Exception {
			LOG.info("Sub job start, jobName={}", jobCtx.getJobName());

			StopWatch stopWatch = StopWatch.startNew();
			out: while (true) {
				stopWatch.mark();

				List<Data> dataList = jobData.next(jobCtx, jobConfig.getJobBatchSize());
				jobResult.addDataLoadElapsedTime(stopWatch.mark()); // 记录加载时间

				for (Data data : dataList) {
					stopWatch.mark();

					// 执行任务，并重试指定次数
					boolean isSuccess = false;
					int retryLimit = jobConfig.getJobRetryLimit();
					while (isSuccess == false && retryLimit-- > 0) {
						try {
							isSuccess = jobWorker.execute(jobCtx, data);
						} catch (Throwable t) {
							LOG.error("Executing sub job error, jobName={}", jobCtx.getJobName(), t);
						}
					}

					if (isSuccess) {
						jobResult.addSucc(data, stopWatch.mark());

					} else {
						jobResult.addFail(data, stopWatch.mark());

						if (jobConfig.isFastFail()) {
							// 快速失败
							LOG.warn("Sub job fast fail, jobName={}", jobCtx.getJobName());
							break out;
						}
					}

					// 任务间隔
					Thread.sleep(jobConfig.getJobInterval());
				}

				if (dataList.size() < jobConfig.getJobBatchSize()) {
					// 无后续数据
					break;
				}
			}

			LOG.info("Sub job finished, jobName={}, count(s/f/t)={}/{}/{}, dataLoadElapsed={}ms, procElapsed={}ms", new Object[] {
					jobCtx.getJobName(), jobResult.getSuccCount(), jobResult.getFailCount(), jobResult.getTotalCount(),
					jobResult.getDataLoadElapsedTime(), jobResult.getProcElapsedTime() });

			return jobResult;
		}

		/**
		 * @return the jobResult
		 */
		public JobResult<Data> getJobResult() {
			return jobResult;
		}
	}
}
