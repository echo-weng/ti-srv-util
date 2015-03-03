package me.andpay.ti.spring.batch;

import java.io.IOException;
import java.util.List;

import me.andpay.ti.util.StringUtil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.Resource;

/**
 * PoiExcel表格写入器类。
 * 
 * @author sea.bao
 */
public class PoiExcelSheetItemWriter<T> implements AbstractPoiExcelSheetItemWriter<T> {
	public static final String DEFAULT_SHEET_NAME = "sheet";

	private DataFormat dataFormat = null;

	private Workbook workbook;

	private Sheet sheet = null;

	private String sheetName = null;

	private PoiExcelSheetHeaderCallback headerCallback;

	private PoiExcelSheetFooterCallback footerCallback;

	private int columnCount = 0;

	private short currentRow = 0;

	private AccumulateFieldExtractor<T> fieldExtractor;

	/* (non-Javadoc)
	 * @see me.andpay.ti.spring.AbstractPoiExcelSheetItemWriter#writeHeader(org.springframework.core.io.Resource, org.apache.poi.ss.usermodel.Workbook, int)
	 */
	public void writeHeader(Resource res, Workbook workbook, int sheetIdx) throws ItemStreamException {
		this.workbook = workbook;

		String sname = sheetName;
		if (sname == null) {
			sname = DEFAULT_SHEET_NAME + sheetIdx;
		}

		sheet = workbook.createSheet(sname);

		columnCount = 0;
		currentRow = 0;

		dataFormat = workbook.createDataFormat();

		if (headerCallback != null) {
			try {
				currentRow = headerCallback.writeHeader(sheet);
			} catch (IOException e) {
				throw new ItemStreamException("Write excel file header meet error, file: [" + res + "].", e);
			}
		}
	}

	protected void writeRow(Object[] objs) {
		if (objs.length > columnCount) {
			columnCount = objs.length;
		}

		Row row = sheet.createRow(currentRow++);
		for (int i = 0; i < objs.length; i++) {
			Cell cell = row.createCell(i);
			Object obj = objs[i];
			if (obj == null) {
				cell.setCellType(Cell.CELL_TYPE_BLANK);
				continue;
			}

			if (obj instanceof java.util.Date) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(StringUtil.format("yyyy-MM-dd HH:mm:ss", (java.util.Date) obj));
			} else if (obj instanceof java.math.BigDecimal) {
				cell.setCellValue(((java.math.BigDecimal) obj).doubleValue());
				CellStyle style = workbook.createCellStyle();
				style.setDataFormat(dataFormat.getFormat("#,##0.00"));
				cell.setCellStyle(style);
			} else {
				if (obj instanceof java.lang.Integer) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(((java.lang.Integer) obj).doubleValue());
				} else if (obj instanceof java.lang.Long) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(((java.lang.Long) obj).doubleValue());
				} else if (obj instanceof java.lang.Short) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(((java.lang.Short) obj).doubleValue());
				} else if (obj instanceof java.lang.Double) {
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((java.lang.Double) obj);
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(obj.toString());
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see me.andpay.ti.spring.AbstractPoiExcelSheetItemWriter#write(java.util.List)
	 */
	public void write(List<? extends T> items) throws Exception {
		for (T item : items) {
			Object[] objs = fieldExtractor.extract(item);
			if (objs == null) {
				continue;
			}

			writeRow(objs);
		}
	}

	/* (non-Javadoc)
	 * @see me.andpay.ti.spring.AbstractPoiExcelSheetItemWriter#writeFooter(org.springframework.core.io.Resource, org.apache.poi.ss.usermodel.Workbook)
	 */
	public void writeFooter(Resource res, Workbook workbook) {
		List<Object[]> objs = fieldExtractor.extractLast();
		if (objs != null) {
			for ( Object[] o : objs ) {
				writeRow(o);
			}
		}

		if (footerCallback != null) {
			try {
				footerCallback.writeFooter(sheet, currentRow);
			} catch (IOException e) {
				throw new ItemStreamException("Write excel file footer meet error, file: [" + res + "].", e);
			}
		}

		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public PoiExcelSheetHeaderCallback getHeaderCallback() {
		return headerCallback;
	}

	public void setHeaderCallback(PoiExcelSheetHeaderCallback headerCallback) {
		this.headerCallback = headerCallback;
	}

	public PoiExcelSheetFooterCallback getFooterCallback() {
		return footerCallback;
	}

	public void setFooterCallback(PoiExcelSheetFooterCallback footerCallback) {
		this.footerCallback = footerCallback;
	}

	public AccumulateFieldExtractor<T> getFieldExtractor() {
		return fieldExtractor;
	}

	public void setFieldExtractor(AccumulateFieldExtractor<T> fieldExtractor) {
		this.fieldExtractor = fieldExtractor;
	}
}
