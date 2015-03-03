package me.andpay.ti.spring.batch;

import java.math.BigDecimal;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;

/**
 * 抽象Spring批次支持类。
 * 
 * @author sea.bao
 */
public abstract class AbstractSpringBatchSupport {
	/**
	 * 任务层次
	 */
	private static final int JOB_LEVEL = 0;

	/**
	 * 步骤层次
	 */
	private static final int STEP_LEVEL = 1;

	/**
	 * 步骤执行器
	 */
	protected StepExecution stepExecution;

	private ExecutionContext getExeContext(int level) {
		if (JOB_LEVEL == level) {
			return stepExecution.getJobExecution().getExecutionContext();
		} else {
			return stepExecution.getExecutionContext();
		}
	}

	private void setContextValue(int level, String name, Object value) {
		getExeContext(level).put(name, value);
	}

	private Object getContextValue(int level, String name, Object defaultValue) {
		Object value = getExeContext(level).get(name);
		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	private void incContextValue(int level, String name, long count) {
		Object value = getContextValue(level, name, 0L);
		if (value instanceof Long) {
			value = ((Long) value).longValue() + 1;
		} else if (value instanceof Integer) {
			value = ((Integer) value).intValue() + 1;
		} else if (value instanceof Short) {
			value = ((Short) value).shortValue() + 1;
		} else {
			throw new RuntimeException("Unsupport type, clazz=[" + value.getClass().getName() + "].");
		}

		setContextValue(level, name, value);
	}

	private void sumToContextValue(int level, String name, BigDecimal amt) {
		Object value = getContextValue(level, name, BigDecimal.ZERO);
		BigDecimal total = (BigDecimal) value;
		total = total.add(amt);
		setContextValue(level, name, total);
	}

	protected void setJobContextValue(String name, Object value) {
		setContextValue(JOB_LEVEL, name, value);
	}

	protected Object getJobContextValue(String name) {
		return getContextValue(JOB_LEVEL, name, null);
	}

	protected Object getJobContextValue(String name, Object defaultValue) {
		return getContextValue(JOB_LEVEL, name, defaultValue);
	}

	protected void incJobContextValue(String name, long count) {
		incContextValue(JOB_LEVEL, name, count);
	}

	protected void incJobContextValue(String name) {
		incContextValue(JOB_LEVEL, name, 1L);
	}

	protected void sumToJobContextValue(String name, BigDecimal amt) {
		sumToContextValue(JOB_LEVEL, name, amt);
	}

	protected void setStepContextValue(String name, Object value) {
		setContextValue(STEP_LEVEL, name, value);
	}

	protected Object getStepContextValue(String name) {
		return getContextValue(STEP_LEVEL, name, null);
	}

	protected Object getStepContextValue(String name, Object defaultValue) {
		return getContextValue(STEP_LEVEL, name, defaultValue);
	}

	protected void incStepContextValue(String name, long count) {
		incContextValue(STEP_LEVEL, name, count);
	}

	protected void incStepContextValue(String name) {
		incContextValue(STEP_LEVEL, name, 1L);
	}

	protected void sumToStepContextValue(String name, BigDecimal amt) {
		sumToContextValue(STEP_LEVEL, name, amt);
	}

	protected JobParameters getJobParameters() {
		return stepExecution.getJobParameters();
	}

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
}