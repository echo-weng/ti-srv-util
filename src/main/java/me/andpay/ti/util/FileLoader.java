package me.andpay.ti.util;

import java.io.IOException;

import me.andpay.ti.util.FileUtil.FileReadHandler;

/**
 * 文件加载器接口定义类。
 * 
 * @author sea.bao
 * 
 * @param <T>
 */
public interface FileLoader<T> {
	/**
	 * 加载文件内容。
	 * 
	 * @param handler
	 * @return
	 */
	T load(FileReadHandler handler) throws IOException;
}
