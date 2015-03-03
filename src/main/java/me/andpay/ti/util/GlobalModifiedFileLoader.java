package me.andpay.ti.util;


/**
 * 全局变化文件加载器类。
 * 
 * @author sea.bao
 */
public class GlobalModifiedFileLoader {
	/**
	 * 加载器
	 */
	private static ModifiedFileLoader mloader = new ModifiedFileLoader();
	
	public static void registerFile(String targetFileName, FileLoader<?> loader) {
		mloader.registerFile(targetFileName, loader);
	}

	public static void registerFile(String targetFileName, long checkInterval, FileLoader<?> loader) {
		mloader.registerFile(targetFileName, checkInterval, loader);
	}
	
	public static void unregisterFile(String targetFileName) {
		mloader.unregisterFile(targetFileName);
	}

	public static <T> T load(String targetFileName, Class<T> dataClazz) {
		return mloader.load(targetFileName, dataClazz);
	}
	
	public static Object load(String targetFileName) {
		return mloader.load(targetFileName, Object.class);
	}
}
