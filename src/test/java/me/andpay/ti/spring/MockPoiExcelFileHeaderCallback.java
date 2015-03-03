package me.andpay.ti.spring;

import java.io.IOException;

import me.andpay.ti.spring.batch.PoiExcelSheetHeaderCallback;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class MockPoiExcelFileHeaderCallback implements PoiExcelSheetHeaderCallback {

	public short writeHeader(Sheet sheet) throws IOException {
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("列1");
		cell = row.createCell(1);
		cell.setCellValue("列2");
		cell = row.createCell(2);
		cell.setCellValue("列3");
		cell = row.createCell(3);
		cell.setCellValue("列4");
		
		return 1;
	}

}
