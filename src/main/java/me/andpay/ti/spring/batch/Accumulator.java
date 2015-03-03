package me.andpay.ti.spring.batch;

import org.springframework.batch.core.StepExecution;

/**
 * 累积器接口定义类。
 * 
 * @author sea.bao
 */
public interface Accumulator<I, O> {
	/**
	 * 累积项目
	 * @param item item为null时候，表示Item读取完毕。
	 * @param stepExecution
	 * @return 返回null，表示继续读取Item
	 */
	O accumulate(I item, StepExecution stepExecution) throws Exception;
}
