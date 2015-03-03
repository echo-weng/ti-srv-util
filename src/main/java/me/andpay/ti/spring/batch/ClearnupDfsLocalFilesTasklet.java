package me.andpay.ti.spring.batch;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * 清除Dfs本地文件任务类。
 * 
 * @author seabao
 * 
 */
public class ClearnupDfsLocalFilesTasklet implements Tasklet {

	protected void deleteFile(String file) {
		File f = new File(file);
		f.delete();
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExeCtx = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext();

		String[] srcFiles = (String[]) jobExeCtx.get(JobContextValueNames.SOURCE_FILES);
		if (srcFiles != null) {
			for (String srcFile : srcFiles) {
				deleteFile(srcFile);
			}
		}

		String dfsLocalFile = jobExeCtx.getString(JobContextValueNames.DFS_LOCAL_FILE);
		if (dfsLocalFile != null) {
			deleteFile(dfsLocalFile);
		}

		return RepeatStatus.FINISHED;
	}

}
