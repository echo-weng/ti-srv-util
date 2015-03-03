package me.andpay.ti.xls.impl.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.andpay.ti.util.IOUtil;
import me.andpay.ti.util.StringUtil;
import me.andpay.ti.xls.XlsReader;
import me.andpay.ti.xls.helper.XlsReadContext;
import me.andpay.ti.xls.helper.XlsRowConverter;
import me.andpay.ti.xls.helper.XlsReadRowCallback;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.util.ResourceUtils;

/**
 * 基于POI的Excel文件读取器实现类
 * 
 * @author alex
 */
public class PoiXlsReader implements XlsReader {
	/**
	 * 日期值的字符串格式
	 */
	private String datePattern = "yyyy-MM-dd HH:mm:ss";

	/**
	 * {@inheritDoc}
	 */
	public <T> void read(String filePath, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback) {
		if (StringUtil.isBlank(filePath)) {
			throw new RuntimeException("Xls file path cannot be blank for reading xls");
		}

		try {
			read(ResourceUtils.getURL(filePath).openStream(), converter, callback);
		} catch (Exception e) {
			throw new RuntimeException("Read xls file error via file path, filePath=" + filePath, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void read(File file, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback) {
		if (file == null) {
			throw new RuntimeException("Xls file cannot be null for reading xls");
		}

		try {
			read(new FileInputStream(file), converter, callback);
		} catch (Exception e) {
			throw new RuntimeException("Read xls file error via file, filePath=" + file.getAbsolutePath(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> void read(InputStream in, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback) {
		if (in == null) {
			throw new RuntimeException("InputStream cannot be null for reading xls");

		} else if (converter == null) {
			throw new RuntimeException("XlsRowConverter cannot be null for reading xls");

		} else if (callback == null) {
			throw new RuntimeException("XlsRowReadCallback cannot be null for reading xls");

		}

		try {
			doRead(in, converter, callback);
		} catch (Exception e) {
			throw new RuntimeException("Read xls file error via stream", e);

		} finally {
			IOUtil.closeQuietly(in);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected <T> void doRead(InputStream in, XlsRowConverter<T> converter, XlsReadRowCallback<T> callback) {
		Workbook workbook = getWorkbook(in);

		XlsReadContext ctx = new XlsReadContext();
		ctx.setSheetCount(workbook.getNumberOfSheets());

		for (int sheetIdx = 0; sheetIdx < ctx.getSheetCount(); sheetIdx++) {
			Sheet sheet = workbook.getSheetAt(sheetIdx);

			ctx.setSheetIndex(sheetIdx);
			ctx.setSheetName(sheet.getSheetName());

			int rowCount = sheet.getLastRowNum() + 1;
			ctx.setRowCount(rowCount);

			for (int rowNum = 0; rowNum < rowCount; rowNum++) {
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}

				int cellCount = row.getLastCellNum(); // 内部+1
				if (cellCount == -1) {
					continue;
				}

				ctx.setRowNum(rowNum);
				ctx.setCellCount(cellCount);

				try {
					// 将行数据转换为字符串集合
					List<String> cellVals = getCellValues(row);

					// 转换为行对象
					T rowObj = converter.convert(cellVals, ctx);

					// 处理行对象
					callback.readRow(rowObj, ctx);
				} catch (Exception ex) {
					throw new RuntimeException(String.format("Read xls row error, sheetName=%s, rowNum=%s",
							ctx.getSheetName(), ctx.getRowNum()), ex);
				}
			}
		}
	}

	/**
	 * 获得工作簿
	 * 
	 * @param in
	 * @return
	 */
	protected Workbook getWorkbook(InputStream in) {
		try {
			return WorkbookFactory.create(in);
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse xls file", e);
		}
	}

	/**
	 * 获取单元格数据集
	 * 
	 * @param row
	 * @param ctx
	 * @return
	 */
	protected List<String> getCellValues(Row row) {
		List<String> cellVals = new ArrayList<String>();

		int cellCount = row.getLastCellNum();
		for (int cellNum = 0; cellNum < cellCount; cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell == null) {
				cellVals.add(null);
				continue;
			}

			cellVals.add(getCellValueAsString(cell, cell.getCellType()));
		}

		return cellVals;
	}

	/**
	 * 获取单元格字符串值
	 * 
	 * @param cell
	 * @param cellType
	 * @return
	 */
	protected String getCellValueAsString(Cell cell, int cellType) {
		switch (cellType) {
			case Cell.CELL_TYPE_NUMERIC:
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					return StringUtil.format(datePattern, cell.getDateCellValue()); // 日期
				}

				return Double.toString(cell.getNumericCellValue()); // 数字

			case Cell.CELL_TYPE_BOOLEAN:
				return Boolean.toString(cell.getBooleanCellValue()); // 布尔值

			case Cell.CELL_TYPE_FORMULA:
				return getCellValueAsString(cell, cell.getCachedFormulaResultType()); // 表达式

			case Cell.CELL_TYPE_ERROR:
				return ErrorEval.getText(cell.getErrorCellValue()); // 表达式错误

			default:
				return StringUtil.emptyAsNull(cell.getStringCellValue()); // 字符串
		}
	}

	/**
	 * @param datePattern
	 *            the datePattern to set
	 */
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
}
