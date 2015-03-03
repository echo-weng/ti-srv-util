package me.andpay.ti.spring;

import java.io.File;

import me.andpay.ti.spring.batch.SpringBatchUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 滚动文件写入器测试类。
 * 
 * @author sea.bao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations = { "classpath:spring-config/ti-srv-util-spring-config.xml" })
public class PoiExcelFileItemWriterTest {
	/**
	 * 任务启动器
	 */
	@Autowired
	private JobLauncher jobLauncher;

	/**
	 * 卸载清分交易数据任务
	 */
	@Autowired
	@Qualifier("ti-test.PoiExcelJob")
	private Job unloadClearTxnDataJob;

	@Test
	public void test() throws Exception {
		String path = System.getProperty("user.home") + "/tmp/excel-data";
		File file = new File(path);
		file.mkdirs();

		String exportFileName = path + "/output.xls";

		JobParametersBuilder builder = new JobParametersBuilder().addString("test-file", exportFileName);
		JobParameters jobParas = builder.toJobParameters();

		SpringBatchUtil.executeJob(jobLauncher, unloadClearTxnDataJob, jobParas);
	}

}
