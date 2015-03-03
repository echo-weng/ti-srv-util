package me.andpay.ti.util;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 定时的Ifttt类。
 * 
 * @author sea.bao
 */
public class TimingIfttt<T> {
	public static interface That<T> {
		void doIt(T checkResult);
	}

	public static interface This<T> {
		T check();
	}

	private Log logger = LogFactory.getLog(this.getClass());
	
	private boolean stopFlag = false;

	private Timer timer;

	private Map<String, TimerTask> timerTasks = new ConcurrentHashMap<String, TimerTask>();

	public TimingIfttt() {
		this.timer = new Timer();
	}

	public void register(final String recipe, long period, final This<T> recipeThis, final That<T> recipeThat) {
		checkStatus();
		
		TimerTask timerTask = timerTasks.remove(recipe);
		if (timerTask != null) {
			timerTask.cancel();
		}

		timerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					T checkResult = recipeThis.check();
					if (checkResult != null) {
						recipeThat.doIt(checkResult);
					}
				} catch (Throwable e) {
					logger.error("TimingIfttt meet error, recipe=[" + recipe + "].", e);
				}
			}

		};

		timer.scheduleAtFixedRate(timerTask, period, period);
		timerTasks.put(recipe, timerTask);
	}

	public void unregister(String recipe) {
		checkStatus();
		
		TimerTask timerTask = timerTasks.remove(recipe);
		if (timerTask != null) {
			timerTask.cancel();
		}
	}
	
	private void checkStatus() {
		if (stopFlag) {
			throw new RuntimeException("The TimingIfttt already stopped.");
		}
	}

	public void stop() {
		stopFlag = true;
		timer.cancel();
	}
}
