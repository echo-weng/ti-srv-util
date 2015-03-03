package me.andpay.ti.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

/**
 * 简单Future实现测试类
 * 
 * @author alex
 */
public class BasicFutureTest {
	@Test
	public void testGet() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();

		// 1 callable
		BasicFuture<Long> bf = new BasicFuture<Long>();
		MyCallable myCall = new MyCallable(bf, false);
		Future<Boolean> future = executor.submit(myCall);
		bf.setTask(future);

		Assert.assertNotNull(bf.get());
		Assert.assertFalse(bf.isCancelled());
		Assert.assertTrue(bf.isDone());
		Assert.assertEquals(Boolean.TRUE, future.get());
		Assert.assertFalse(future.isCancelled());
		Assert.assertTrue(future.isDone());

		// 2 runnable
		bf = new BasicFuture<Long>();
		MyRunnable myRun = new MyRunnable(bf, false);
		Thread thread = new Thread(myRun);
		thread.start();

		Assert.assertNotNull(bf.get());
		Assert.assertFalse(bf.isCancelled());
		Assert.assertTrue(bf.isDone());
	}

	@Test
	public void testErrorOccur() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();

		// 1 callable
		BasicFuture<Long> bf = new BasicFuture<Long>();
		MyCallable myCall = new MyCallable(bf, true);
		Future<Boolean> future = executor.submit(myCall);
		bf.setTask(future);
		try {
			bf.get();
			Assert.fail("Expect exception");
		} catch (ExecutionException e) {
		}
		Assert.assertFalse(bf.isCancelled());
		Assert.assertTrue(bf.isDone());

		try {
			future.get();
			Assert.fail("Expect exception");
		} catch (ExecutionException e) {
		}
		Assert.assertFalse(future.isCancelled());
		Assert.assertTrue(future.isDone());

		// 2 runnable
		bf = new BasicFuture<Long>();
		MyRunnable myRun = new MyRunnable(bf, true);
		Thread thread = new Thread(myRun);
		thread.start();

		try {
			bf.get();
			Assert.fail("Expect exception");
		} catch (ExecutionException e) {
		}
		Assert.assertFalse(bf.isCancelled());
		Assert.assertTrue(bf.isDone());
	}

	@Test
	public void testCancel() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();

		// 1 callable - not started
		BasicFuture<Long> bf = new BasicFuture<Long>();
		MyCallable myCall = new MyCallable(bf, false);
		Future<Boolean> future = executor.submit(myCall);
		bf.setTask(future);

		// task hasn't been started
		Assert.assertTrue(bf.cancel(true));

		try {
			bf.get();
			Assert.fail("Expect cancelled");
		} catch (Exception ex) {
		}
		Assert.assertTrue(bf.isCancelled());
		Assert.assertTrue(bf.isDone());

		try {
			future.get();
			Assert.fail("Expect cancelled");
		} catch (Exception ex) {
		}
		Assert.assertTrue(future.isCancelled());
		Assert.assertTrue(future.isDone());

		Assert.assertFalse(myCall.start);
		Assert.assertFalse(myCall.end);
		Assert.assertFalse(myCall.interrupted);

		// 2 callable - started
		bf = new BasicFuture<Long>();
		myCall = new MyCallable(bf, false);
		future = executor.submit(myCall);
		bf.setTask(future);

		SleepUtil.sleep(100); // wait for starting
		Assert.assertTrue(bf.cancel(true));
		SleepUtil.sleep(200); // wait for interruption

		try {
			bf.get();
			Assert.fail("Expect cancelled");
		} catch (Exception ex) {
		}
		Assert.assertTrue(bf.isCancelled());
		Assert.assertTrue(bf.isDone());

		try {
			future.get();
			Assert.fail("Expect cancelled");
		} catch (Exception ex) {
		}
		Assert.assertTrue(future.isCancelled());
		Assert.assertTrue(future.isDone());

		Assert.assertTrue(myCall.start);
		Assert.assertFalse(myCall.end);
		Assert.assertTrue(myCall.interrupted);

		// 3 callable - finished
		bf = new BasicFuture<Long>();
		myCall = new MyCallable(bf, false);
		future = executor.submit(myCall);
		bf.setTask(future);

		SleepUtil.sleep(300); // wait for finishing
		Assert.assertFalse(bf.cancel(true));

		Assert.assertNotNull(bf.get());
		Assert.assertFalse(bf.isCancelled());
		Assert.assertTrue(bf.isDone());

		Assert.assertEquals(Boolean.TRUE, future.get());
		Assert.assertFalse(future.isCancelled());
		Assert.assertTrue(future.isDone());

		Assert.assertTrue(myCall.start);
		Assert.assertTrue(myCall.end);
		Assert.assertFalse(myCall.interrupted);

		// 4 runnable - started 
		bf = new BasicFuture<Long>();
		MyRunnable myRun = new MyRunnable(bf, false);
		Thread thread = new Thread(myRun);
		thread.start();

		SleepUtil.sleep(100); // wait for starting
		Assert.assertTrue(bf.cancel(true));
		SleepUtil.sleep(200); // wait for interruption

		try {
			bf.get();
			Assert.fail("Expect cancelled");
		} catch (Exception ex) {
		}
		Assert.assertTrue(bf.isCancelled());
		Assert.assertTrue(bf.isDone());

		Assert.assertTrue(myRun.start);
		Assert.assertFalse(myRun.end);
		Assert.assertTrue(myRun.interrupted);
	}

	static class MyCallable implements Callable<Boolean> {
		BasicFuture<Long> bf;
		boolean start;
		boolean end;
		boolean interrupted;
		boolean errOccured;

		public MyCallable(BasicFuture<Long> bf, boolean errOccured) {
			this.bf = bf;
			this.errOccured = errOccured;
		}

		public Boolean call() throws Exception {
			start = true;
			System.out.println("[Callable]Start calling, sleep 200ms");
			try {
				SleepUtil.sleep(200L);
			} catch (Exception e) {
				interrupted = true;
				System.out.println("[Callable]Sleep interrupted");
				throw e;
			}

			System.out.println("[Callable]Finish calling, return true");

			if (errOccured) {
				RuntimeException ex = new RuntimeException();
				bf.failed(ex);
				throw ex;

			} else {
				bf.completed(System.currentTimeMillis());
			}

			end = true;
			return true;
		}
	}

	static class MyRunnable implements Runnable {
		BasicFuture<Long> bf;
		boolean start;
		boolean end;
		boolean interrupted;
		boolean errOccured;

		public MyRunnable(BasicFuture<Long> bf, boolean errOccured) {
			this.bf = bf;
			this.errOccured = errOccured;
		}

		public void run() {
			bf.catchRunner(); // catch for interruption

			start = true;
			System.out.println("[Runnable]Start calling, sleep 200ms");
			try {
				SleepUtil.sleep(200L);
			} catch (Exception e) {
				interrupted = true;
				System.out.println("[Runnable]Sleep interrupted");
				throw new RuntimeException(e);
			}

			System.out.println("[Runnable]Finish calling, return true");

			if (errOccured) {
				RuntimeException ex = new RuntimeException();
				bf.failed(ex);
				throw ex;

			} else {
				bf.completed(System.currentTimeMillis());
			}

			end = true;
		}
	}
}
