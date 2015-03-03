package me.andpay.ti.spring;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

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
public class RollingFileItemWriterTest {
	/**
	 * 任务启动器
	 */
	@Autowired
	private JobLauncher jobLauncher;

	/**
	 * 卸载清分交易数据任务
	 */
	@Autowired
	@Qualifier("ti-test.RollingDumpJob")
	private Job unloadClearTxnDataJob;

	private int countFile(String file) throws IOException {
		FileReader rd = new FileReader(file);
		LineNumberReader lrd = new LineNumberReader(rd);
		int l = 0;

		while (lrd.readLine() != null) {
			l++;
		}

		lrd.close();

		return l;
	}

	@Test
	public void test() throws Exception {
		String path = System.getProperty("user.home") + "/tmp/rolling-data";
		File file = new File(path);
		file.mkdirs();

		String exportFileName = path + "/output.txt";

		JobParametersBuilder builder = new JobParametersBuilder().addString("test-file", exportFileName);
		JobParameters jobParas = builder.toJobParameters();

		SpringBatchUtil.executeJob(jobLauncher, unloadClearTxnDataJob, jobParas);

		assertEquals(11, countFile(path + "/output.txt"));
		assertEquals(11, countFile(path + "/output.1.txt"));
		assertEquals(4, countFile(path + "/output.2.txt"));
	}

}
