package me.andpay.ti.spring.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * 滚动多文件写入器类。
 * 
 * @author sea.bao
 * 
 * @param <T>
 */
public class RollingFileItemWriter<T> extends FlatFileItemWriter<T> {
	public static final String CURRENT_ROW_COUNT = "current.row-count";

	public static final String CURRENT_ROLLING_COUNT = "current.rolling-count";

	/**
	 * 缺省文件列数量
	 */
	public static final int DEFAULT_ROW_COUNT = 999;

	/**
	 * 一个文件最长列数量
	 */
	private int maxRowCount = DEFAULT_ROW_COUNT;

	/**
	 * 当前列计数
	 */
	private int currentRowCount = 0;

	/**
	 * 当前滚动计数器
	 */
	private int currentRollingCount = 0;

	/**
	 * 文件名称
	 */
	private String fileName;

	protected void rollingResource() {
		super.close();

		currentRollingCount++;
		currentRowCount = 0;

		String s = fileName;

		int idx = s.lastIndexOf(".");
		if (idx >= 0) {
			StringBuffer sb = new StringBuffer(s.substring(0, idx));
			sb.append(".");
			sb.append(currentRollingCount);
			sb.append(".");
			sb.append(s.substring(idx + 1));

			s = sb.toString();
		} else {
			s = s + "." + currentRollingCount;
		}

		Resource resource = new FileSystemResource(s);
		super.setResource(resource);

		super.newFile();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void write(List<? extends T> items) throws Exception {
		int c = currentRowCount;
		if ((c + items.size()) > maxRowCount) {
			List items1 = new ArrayList();
			int i = 0;
			for (; i < (maxRowCount - c); i++) {
				items1.add(items.get(i));
			}

			if (items1.isEmpty() == false) {
				currentRowCount += items1.size();
				super.write(items1);
			}

			rollingResource();

			items1.clear();
			for (; i < items.size(); i++) {
				items1.add(items.get(i));
			}

			if (items1.isEmpty() == false) {
				currentRowCount += items1.size();
				super.write(items1);
			}
		} else {
			currentRowCount += items.size();
			super.write(items);
		}
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		super.open(executionContext);

		String keyForCurrentRowCount = getKey(CURRENT_ROW_COUNT);
		String keyForCurrentRollingCount = getKey(CURRENT_ROLLING_COUNT);

		if (executionContext.containsKey(keyForCurrentRowCount)) {
			currentRowCount = executionContext.getInt(keyForCurrentRowCount);
			currentRollingCount = executionContext.getInt(keyForCurrentRollingCount);
		} else {
			currentRowCount = 0;
			currentRollingCount = 0;
		}
	}

	@Override
	public void update(ExecutionContext executionContext) {
		super.update(executionContext);

		String keyForCurrentRowCount = getKey(CURRENT_ROW_COUNT);
		String keyForCurrentRollingCount = getKey(CURRENT_ROLLING_COUNT);

		executionContext.put(keyForCurrentRowCount, currentRowCount);
		executionContext.put(keyForCurrentRollingCount, currentRollingCount);
	}

	@Override
	public void setResource(Resource resource) {
		try {
			this.fileName = resource.getFile().getPath();
		} catch (IOException e) {
			throw new RuntimeException("Rolling resource meet error.", e);
		}

		super.setResource(resource);
	}

	public int getMaxRowCount() {
		return maxRowCount;
	}

	public void setMaxRowCount(int maxRowCount) {
		this.maxRowCount = maxRowCount;
	}

}
