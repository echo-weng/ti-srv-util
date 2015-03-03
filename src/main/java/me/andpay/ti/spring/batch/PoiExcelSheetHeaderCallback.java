package me.andpay.ti.spring.batch;

import java.io.IOException;
import java.io.Writer;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * PoiExcelSheet头回调接口定义类。
 * 
 * @author sea.bao
 */
public interface PoiExcelSheetHeaderCallback {
	/**
	 * Write contents to a file using the supplied {@link Writer}. It is not
	 * required to flush the writer inside this method.
	 */
	short writeHeader(Sheet sheet) throws IOException;
}
