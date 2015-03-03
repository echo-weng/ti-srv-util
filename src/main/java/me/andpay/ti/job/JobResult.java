package me.andpay.ti.job;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务执行结果类
 * 
 * @author alex
 */
public class JobResult<Data> {
	/**
	 * 是否所有数据处理完成
	 */
	private boolean done;

	/**
	 * 只记录成功失败次数
	 */
	private boolean recordCountOnly = true;

	/**
	 * 任务计数
	 */
	private int jobCount = 1;

	/**
	 * 成功计数
	 */
	private int succCount;

	/**
	 * 失败计数
	 */
	private int failCount;

	/**
	 * 数据加载消耗时间
	 */
	private long dataLoadElapsedTime;

	/**
	 * 任务处理耗时时间
	 */
	private long procElapsedTime;

	/**
	 * 成功数据
	 */
	private List<Data> succData = new ArrayList<Data>();

	/**
	 * 失败数据
	 */
	private List<Data> failData = new ArrayList<Data>();

	/**
	 * 构造函数
	 * 
	 * @param recordCountOnly
	 */
	public JobResult() {
	}

	/**
	 * 构造函数
	 * 
	 * @param recordCountOnly
	 */
	public JobResult(boolean recordCountOnly) {
		this.recordCountOnly = recordCountOnly;
	}

	/**
	 * 准备数据汇总
	 * 
	 * @return
	 */
	public JobResult<Data> readyForSum() {
		jobCount = 0;
		succCount = 0;
		failCount = 0;
		dataLoadElapsedTime = 0L;
		procElapsedTime = 0L;
		succData.clear();
		failData.clear();

		return this;
	}

	/**
	 * 汇总任务执行结果
	 * 
	 * @param jobResult
	 */
	public void summarize(JobResult<Data> jobResult) {
		if (jobResult == null) {
			return;
		}

		if (recordCountOnly == false) {
			this.succData.addAll(jobResult.succData);
			this.failData.addAll(jobResult.failData);
		}

		this.jobCount += jobResult.jobCount;
		this.succCount += jobResult.succCount;
		this.failCount += jobResult.failCount;

		this.dataLoadElapsedTime += jobResult.dataLoadElapsedTime;
		this.procElapsedTime += jobResult.procElapsedTime;
	}

	/**
	 * 任务数
	 * 
	 * @return
	 */
	public int getJobCount() {
		return jobCount;
	}

	/**
	 * 增加数据加载时间
	 * 
	 * @param dataLoadElapsedTime
	 */
	public void addDataLoadElapsedTime(long dataLoadElapsedTime) {
		this.dataLoadElapsedTime += dataLoadElapsedTime;
	}

	/**
	 * 添加成功数据
	 * 
	 * @param data
	 */
	public void addSucc(Data data) {
		addSucc(data, 0);
	}

	/**
	 * 添加成功数据
	 * 
	 * @param data
	 * @param procElapsedTime
	 *            处理时间
	 */
	public void addSucc(Data data, long procElapsedTime) {
		if (recordCountOnly == false) {
			succData.add(data);
		}
		succCount++;
		this.procElapsedTime += procElapsedTime;
	}

	/**
	 * 添加成功数据
	 * 
	 * @param dataList
	 */
	public void addSuccList(List<Data> dataList) {
		addSuccList(dataList, 0);
	}

	/**
	 * 添加成功数据
	 * 
	 * @param dataList
	 * @param procElapsedTime
	 *            处理时间
	 */
	public void addSuccList(List<Data> dataList, long procElapsedTime) {
		if (recordCountOnly == false) {
			succData.addAll(dataList);
		}
		succCount += dataList.size();
		this.procElapsedTime += procElapsedTime;
	}

	/**
	 * 添加失败数据
	 * 
	 * @param data
	 */
	public void addFail(Data data) {
		addFail(data, 0);
	}

	/**
	 * 添加失败数据
	 * 
	 * @param data
	 * @param procElapsedTime
	 *            处理时间
	 */
	public void addFail(Data data, long procElapsedTime) {
		if (recordCountOnly == false) {
			failData.add(data);
		}
		failCount++;
		this.procElapsedTime += procElapsedTime;
	}

	/**
	 * 添加失败数据
	 * 
	 * @param data
	 */
	public void addFailList(List<Data> dataList) {
		addFailList(dataList, 0);
	}

	/**
	 * 添加失败数据
	 * 
	 * @param data
	 * @param procElapsedTime
	 *            处理时间
	 */
	public void addFailList(List<Data> dataList, long procElapsedTime) {
		if (recordCountOnly == false) {
			failData.addAll(dataList);
		}
		failCount += dataList.size();
		this.procElapsedTime += procElapsedTime;
	}

	/**
	 * 总数据数
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return succCount + failCount;
	}

	/**
	 * 成功记录数
	 * 
	 * @return
	 */
	public int getSuccCount() {
		return succCount;
	}

	/**
	 * 失败记录数
	 * 
	 * @return
	 */
	public int getFailCount() {
		return failCount;
	}

	/**
	 * @return the succData
	 */
	public List<Data> getSuccData() {
		return succData;
	}

	/**
	 * @return the failData
	 */
	public List<Data> getFailData() {
		return failData;
	}

	/**
	 * @return the done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * @param done
	 *            the done to set
	 */
	public void setDone(boolean done) {
		this.done = done;
	}

	/**
	 * @return the recordCountOnly
	 */
	public boolean isRecordCountOnly() {
		return recordCountOnly;
	}

	/**
	 * @return the dataLoadElapsedTime
	 */
	public long getDataLoadElapsedTime() {
		return dataLoadElapsedTime;
	}

	/**
	 * @return the procElapsedTime
	 */
	public long getProcElapsedTime() {
		return procElapsedTime;
	}
}
