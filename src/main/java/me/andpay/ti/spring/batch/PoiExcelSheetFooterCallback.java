package me.andpay.ti.spring.batch;

import java.io.IOException;
import java.io.Writer;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * PoiExcelSheet尾回调接口定义类。
 * 
 * @author sea.bao
 */
public interface PoiExcelSheetFooterCallback {
	/**
	 * Write contents to a file using the supplied {@link Writer}. It is not
	 * required to flush the writer inside this method.
	 */
	void writeFooter(Sheet sheet, int currentRow) throws IOException;
}
