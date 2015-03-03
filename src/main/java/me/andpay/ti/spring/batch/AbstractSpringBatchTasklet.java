package me.andpay.ti.spring.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * 抽象SpringBatch任务类。
 * 
 * @author sea.bao
 */
public abstract class AbstractSpringBatchTasklet extends AbstractSpringBatchSupport implements Tasklet {
	/**
	 * 内部执行方法
	 * 
	 * @param contribution
	 * @param chunkContext
	 * @return
	 * @throws Exception
	 */
	protected abstract RepeatStatus doExecute(StepContribution contribution, ChunkContext chunkContext)
			throws Exception;

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		super.saveStepExecution(chunkContext.getStepContext().getStepExecution());
		return doExecute(contribution, chunkContext);
	}

}
