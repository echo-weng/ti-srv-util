package me.andpay.ti.util;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.andpay.ti.util.FileUtil.FileReadHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可变化文件加载器类。
 * 
 * @author sea.bao
 */
public class ModifiedFileLoader {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private class ModifiedFileItem {
		/**
		 * 目标文件名称
		 */
		private String targetFileName;

		/**
		 * 文件上次修改时间
		 */
		private long fileLastModified;

		/**
		 * 文件长度
		 */
		private long fileSize;

		/**
		 * 加载的数据
		 */
		private volatile Object loadedData;
	}

	/**
	 * 停止标志
	 */
	private boolean stopFlag = false;

	/**
	 * 定时的ifttt
	 */
	private TimingIfttt<ModifiedFileItem> ifttt = new TimingIfttt<ModifiedFileItem>();

	/**
	 * 加载数据集
	 */
	private Map<String, ModifiedFileItem> loadedDatas = new ConcurrentHashMap<String, ModifiedFileItem>();

	private void loadFile(ModifiedFileItem item, FileLoader<?> loader) {
		File file = new File(item.targetFileName);
		if (file.exists()) {
			FileReadHandler handler = null;
			Object data = null;

			try {
				handler = FileUtil.openForRead(file);
				data = loader.load(handler);
			} catch (Throwable e) {
				// 加载失败，记录错误日志，返回之前加载的数据。
				logger.error("Fail to load modifiedFile=[" + item.targetFileName + "].");
				return;
			} finally {
				if (handler != null) {
					FileUtil.close(handler);
				}
			}

			item.fileLastModified = file.lastModified();
			item.fileSize = file.length();
			item.loadedData = data;
		} else {
			item.loadedData = null;
		}
	}

	public void stop() {
		stopFlag = true;
		ifttt.stop();
	}

	private void checkStatus() {
		if (stopFlag) {
			throw new RuntimeException("The loader already stopped.");
		}
	}
	
	private String getIftttRecipeName(String targetFileName) {
		return "[Load " + targetFileName + "]";
	}
	
	public void unregisterFile(String targetFileName) {
		checkStatus();
		
		ifttt.unregister(getIftttRecipeName(targetFileName));
		loadedDatas.remove(targetFileName);
	}

	public void registerFile(String targetFileName, FileLoader<?> loader) {
		registerFile(targetFileName, 1000L, loader);
	}

	public void registerFile(final String targetFileName, long checkInterval, final FileLoader<?> loader) {
		checkStatus();

		ModifiedFileItem item = new ModifiedFileItem();
		item.targetFileName = targetFileName;
		loadFile(item, loader);
		loadedDatas.put(targetFileName, item);

		ifttt.register(getIftttRecipeName(targetFileName), checkInterval, new TimingIfttt.This<ModifiedFileItem>() {
			public ModifiedFileItem check() {
				ModifiedFileItem item = loadedDatas.get(targetFileName);
				File file = new File(targetFileName);
				if (file.exists() == false) {
					// 文件不存在，启动加载数据过程
					return item;
				}

				if (item.fileSize == file.length() && item.fileLastModified == file.lastModified()) {
					// 没有更新，不启动加载数据过程
					return null;
				}

				// 启动加载数据过程
				return item;
			}
		}, new TimingIfttt.That<ModifiedFileItem>() {
			public void doIt(ModifiedFileItem item) {
				loadFile(item, loader);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String targetFileName, Class<T> dataClazz) {
		return (T) load(targetFileName);
	}

	public Object load(String targetFileName) {
		checkStatus();

		ModifiedFileItem item = loadedDatas.get(targetFileName);
		if (item == null) {
			throw new RuntimeException("Unregistered targetFileName=[" + targetFileName + "].");
		}

		return item.loadedData;
	}
}
