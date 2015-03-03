package me.andpay.ti.spring;

import static org.easymock.EasyMock.*;

import me.andpay.ti.spring.batch.SpringBatchUtil;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * Spring批次工具测试类。
 * 
 * @author sea.bao
 */
public class SpringBatchUtilTest {

	@Test
	public void test() throws Exception {
		JobExecution jobExe = new JobExecution(1L);
		Throwable e = new IllegalArgumentException("abc");
		jobExe.addFailureException(e);
		
		JobLauncher jobLauncher = createMock(JobLauncher.class);
		
		expect(jobLauncher.run(anyObject(Job.class), anyObject(JobParameters.class))).andReturn(jobExe).anyTimes();
		replay(jobLauncher);
		SpringBatchUtil.executeJob(jobLauncher, null, null, IllegalArgumentException.class);
		
		try {
			SpringBatchUtil.executeJob(jobLauncher, null, null);
			Assert.fail("not throw ex.");
		} catch(IllegalArgumentException e1) {
		}
	}

}
