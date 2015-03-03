package me.andpay.ti.spring.batch;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.Resource;

/**
 * 抽象PoiExcel表单Item写入器接口定义类。
 * 
 * @author seabao
 *
 * @param <T>
 */
public interface AbstractPoiExcelSheetItemWriter<T> {
	/**
	 * 写入文件头
	 * @param res
	 * @param workbook
	 * @param sheetIdx
	 * @throws ItemStreamException
	 */
	void writeHeader(Resource res, Workbook workbook, int sheetIdx) throws ItemStreamException;

	/**
	 * 写入文件项目
	 * @param items
	 * @throws Exception
	 */
	void write(List<? extends T> items) throws Exception;

	/**
	 * 写入文件尾
	 * @param res
	 * @param workbook
	 */
	void writeFooter(Resource res, Workbook workbook);
}