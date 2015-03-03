package me.andpay.ti.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import me.andpay.ti.util.SleepUtil;
import me.andpay.ti.util.StringUtil;

import org.junit.Test;

/**
 * 多线程的任务执行器测试类
 * 
 * @author alex
 */
public class MultiThreadJobInvokerTest {

	@Test
	public void test() {
		final int dataSize = 50;

		// Data
		final List<Integer> nums = new ArrayList<Integer>();
		while (nums.size() < dataSize) {
			nums.add(nums.size());
		}

		// JobData<Integer> jobData = new MemoryJobData<Integer>(nums);
		JobData<Integer> jobData = new DefaultJobData<Integer>(new JobDataLoader<Integer>() {
			public List<Integer> load(JobContext jobCtx, int firstIndex, int dataSize) {
				randomSleep();

				if (firstIndex >= nums.size()) {
					return Collections.emptyList();
				}

				return nums.subList(firstIndex, Math.min(nums.size(), firstIndex + dataSize));
			}
		});

		// Worker
		JobWorker<Integer> jobWorker = new JobWorker<Integer>() {
			public boolean execute(JobContext jobCtx, Integer data) {
				try {
					randomSleep();
				} catch (Exception e) {
					System.out.printf("Execute error, data=%s, errMsg=%s%n", data, e.getMessage());
					return false;
				}

				// System.out.println("Execute, data=" + data);
				return (data.intValue() % 2 == 0);
			}
		};

		// ResultHandler
		JobResultHandler<Integer> jobResultHandler = new JobResultHandler<Integer>() {
			public void onComplete(JobResult<Integer> jobResult) {
				System.out.printf("Job complete, done=%s%n", jobResult.isDone());
				System.out.printf("Job succ, data=%s%n", StringUtil.join(jobResult.getSuccData()));
				System.out.printf("Job fail, data=%s%n", StringUtil.join(jobResult.getFailData()));
				Assert.assertEquals(true, jobResult.isDone());
				Assert.assertEquals(dataSize, jobResult.getTotalCount());
				Assert.assertEquals(dataSize / 2, jobResult.getSuccCount());
				Assert.assertEquals(dataSize / 2, jobResult.getSuccData().size());
				Assert.assertEquals(dataSize / 2, jobResult.getFailCount());
				Assert.assertEquals(dataSize / 2, jobResult.getSuccData().size());
			}
		};

		// JobConfig
		JobConfig jobConfig = new JobConfig();
		jobConfig.setAsyncInvoking(false);
		jobConfig.setJobBatchSize(5);
		jobConfig.setJobWorkerSize(2);
		jobConfig.setJobInterval(10L);
		jobConfig.setRecordCountOnly(false);
		jobConfig.setJobRetryLimit(3);
		jobConfig.setJobTimeout(5);

		// JobContext
		JobContext jobCtx = new JobContext();
		jobCtx.setJobName("test");

		// Invoker
		MultiThreadJobInvoker<Integer> invoker = new MultiThreadJobInvoker<Integer>();
		invoker.setJobWorker(jobWorker);
		invoker.setJobConfig(jobConfig);

		invoker.invoke(jobCtx, jobData, jobResultHandler);
	}

	/**
	 * 随机睡眠(5 - 100ms以内)
	 */
	private void randomSleep() {
		SleepUtil.sleep(5 + new Random().nextInt(96));
	}
}
