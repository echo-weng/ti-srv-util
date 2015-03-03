package me.andpay.ti.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 简单Future实现类
 * 
 * @author alex
 */
public class BasicFuture<T> implements Future<T> {
	/**
	 * 是否已完成
	 */
	private volatile boolean completed;

	/**
	 * 是否被取消
	 */
	private volatile boolean cancelled;

	/**
	 * 执行结果
	 */
	private volatile T result;

	/**
	 * 执行异常
	 */
	private volatile Exception ex;

	/**
	 * 实际任务
	 */
	private volatile Future<?> task;

	/**
	 * 运行线程
	 */
	private volatile Thread runner;

	/**
	 * 执行成功
	 * 
	 * @param result
	 */
	public boolean completed(final T result) {
		synchronized (this) {
			if (completed) {
				return false;
			}
			completed = true;
			runner = null;
			this.result = result;
			notifyAll();
		}

		return true;
	}

	/**
	 * 执行失败
	 * 
	 * @param exception
	 */
	public boolean failed(final Exception exception) {
		synchronized (this) {
			if (completed) {
				return false;
			}
			completed = true;
			runner = null;
			ex = exception;
			notifyAll();
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDone() {
		return completed;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean cancel(final boolean mayInterruptIfRunning) {
		synchronized (this) {
			if (completed) {
				return false;
			}

			completed = true;
			try {
				cancelled = (task != null ? task.cancel(mayInterruptIfRunning) : true);
			} catch (Exception ex) {
			}

			if (mayInterruptIfRunning) {
				if (runner != null) {
					try {
						runner.interrupt();
					} catch (Exception ex) {
					}
					cancelled = true;
				}
			}
			runner = null;

			notifyAll();
		}

		return cancelled;
	}

	/**
	 * 捕获运行线程 - 用于cancel
	 */
	public void catchRunner() {
		synchronized (this) {
			if (completed) {
				return;
			}

			runner = Thread.currentThread();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized T get() throws InterruptedException, ExecutionException {
		while (completed == false) {
			wait();
		}

		return getResult();
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException,
			TimeoutException {
		final long msecs = unit.toMillis(timeout);
		final long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
		long waitTime = msecs;
		if (completed) {
			return getResult();
		} else if (waitTime <= 0) {
			throw new TimeoutException();
		} else {
			for (;;) {
				wait(waitTime);
				if (completed) {
					return getResult();
				} else {
					waitTime = msecs - (System.currentTimeMillis() - startTime);
					if (waitTime <= 0) {
						throw new TimeoutException();
					}
				}
			}
		}
	}

	/**
	 * 获得执行结果
	 * 
	 * @throws CancellationException
	 * @throws ExecutionException
	 */
	private T getResult() throws ExecutionException {
		if (cancelled) {
			throw new CancellationException();
		}

		if (ex != null) {
			throw new ExecutionException(ex);
		}

		return result;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(Future<?> task) {
		this.task = task;
	}
}
