package me.andpay.ti.job;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务详细信息对象
 * 
 * @author alex
 */
public class JobContext {
	/**
	 * Null元素常量值
	 */
	private static final String NULL = "_NULL_";

	/**
	 * 任务名称
	 */
	private String jobName;

	/**
	 * 任务触发时间
	 */
	private Date fireTime;

	/**
	 * 任务结束时间
	 */
	private Date finishTime;

	/**
	 * 属性集合[属性名 - 属性值]
	 */
	private Map<String, Object> properties = new ConcurrentHashMap<String, Object>();

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName
	 *            the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the fireTime
	 */
	public Date getFireTime() {
		return fireTime;
	}

	/**
	 * @param fireTime
	 *            the fireTime to set
	 */
	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	/**
	 * @return the finishTime
	 */
	public Date getFinishTime() {
		return finishTime;
	}

	/**
	 * @param finishTime
	 *            the finishTime to set
	 */
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * 获取任务执行时间
	 * 
	 * @return
	 */
	public long getElapsedTime() {
		if (fireTime != null && finishTime != null) {
			return finishTime.getTime() - fireTime.getTime();
		}

		return -1;
	}

	/**
	 * 设置属性值
	 * 
	 * @param propName
	 * @param propValue
	 * @return
	 */
	public Object setProperty(String propName, Object propValue) {
		return properties.put((String) convert(propName), convert(propValue));
	}

	/**
	 * 获得属性值
	 * 
	 * @param propName
	 * @return
	 */
	public Object getProperty(String propName) {
		return reconvert(properties.get(convert(propName)));
	}

	/**
	 * 将null值转换为NULL
	 * 
	 * @param src
	 * @return
	 */
	private Object convert(Object src) {
		return (src == null ? NULL : src);
	}

	/**
	 * 将NULL转换回null
	 * 
	 * @param target
	 * @return
	 */
	private Object reconvert(Object target) {
		return (NULL.equals(target) ? null : target);
	}
}
