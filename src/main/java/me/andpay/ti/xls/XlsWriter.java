package me.andpay.ti.xls;

import java.io.OutputStream;

import me.andpay.ti.xls.model.PartValues;

/**
 * xls 写入接口
 * 
 * @author echo.weng
 * @since 2014年11月7日
 */
public interface XlsWriter {
	
	/**
	 * 生成xls文件
	 * 
	 * @param outputStream 输出位置
	 * @param values 值，values的size对应 隐射文件中parts的 size
	 */
	void write(OutputStream outputStream, PartValues values, String templePath);
	
	/**
	 * 生成xls文件
	 * 
	 * @param writeFilePath 写入的文件路径
	 * @param values
	 * @param templePath
	 * @throws Exception
	 */
	void write(String writeFilePath, PartValues values, String templePath);
	
	/**
	 * xls插入行
	 * 
	 * @param writeFilePath
	 * @param value
	 * @param templePath
	 * @param rowIndex
	 * @param partIndex
	 */
	void writeInsert(String writeFilePath, Object value, String templePath, int rowIndex, int partIndex);
	
}
