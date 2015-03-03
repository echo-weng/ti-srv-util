package me.andpay.ti.spring.batch;

import org.springframework.batch.core.StepExecution;

/**
 * 抽象累积器类。
 * 
 * @author sea.bao
 *
 * @param <I>
 * @param <O>
 */
public abstract class AbstractAccumulator<I,O> extends AbstractSpringBatchSupport implements Accumulator<I, O> {
	protected abstract O doAccumulate(I item, StepExecution stepExecution) throws Exception;
	
	public O accumulate(I item, StepExecution stepExecution) throws Exception {
		super.saveStepExecution(stepExecution);
		return doAccumulate(item, stepExecution);
	}

}
