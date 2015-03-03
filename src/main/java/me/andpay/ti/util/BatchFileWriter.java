package me.andpay.ti.util;

import java.io.PrintWriter;

/**
 * 批量文件工具类。
 * 
 * @author sea.bao
 */
public class BatchFileWriter {
	/**
	 * 分隔符
	 */
	private String delimiter = null;
	
	/**
	 * 写入器
	 */
	private PrintWriter writer;

	public BatchFileWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public BatchFileWriter(PrintWriter writer, String delimiter) {
		this.writer = writer;
		this.delimiter = delimiter;
	}

	public void printField(String fmt, Object... args) {
		writer.printf(fmt, args);
		if (delimiter != null) {
			writer.print(delimiter);
		}
	}

	public void printLastField(String fmt, Object... args) {
		writer.printf(fmt, args);
	}
	
	public void flush() {
		writer.flush();
	}

	public String getDelimiter() {
		return delimiter;
	}

	public PrintWriter getWriter() {
		return writer;
	}
}
