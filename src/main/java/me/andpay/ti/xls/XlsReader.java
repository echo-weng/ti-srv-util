package me.andpay.ti.xls;

import java.io.File;
import java.io.InputStream;

import me.andpay.ti.xls.helper.XlsRowConverter;
import me.andpay.ti.xls.helper.XlsReadRowCallback;

/**
 * Excel文件读取器接口
 * 
 * @author alex
 */
public interface XlsReader {
	/**
	 * 按行读取XLS数据
	 * 
	 * @param in
	 * @param converter
	 * @param callback
	 */
	<T> void read(InputStream in, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback);

	/**
	 * 按行读取数据
	 * 
	 * @param file
	 * @param converter
	 * @param callback
	 */
	<T> void read(File file, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback);

	/**
	 * 按行读取XLS数据
	 * 
	 * @param filePath
	 * @param converter
	 * @param callback
	 */
	<T> void read(String filePath, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback);
}
