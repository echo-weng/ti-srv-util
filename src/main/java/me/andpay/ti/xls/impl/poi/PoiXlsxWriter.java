package me.andpay.ti.xls.impl.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import me.andpay.ti.util.CloseUtil;
import me.andpay.ti.util.CollectionUtil;
import me.andpay.ti.util.StringUtil;
import me.andpay.ti.xls.exception.PoiXlsxException;
import me.andpay.ti.xls.impl.AbstractXlsWriter;
import me.andpay.ti.xls.model.Cell;
import me.andpay.ti.xls.model.Font;
import me.andpay.ti.xls.model.Part;
import me.andpay.ti.xls.model.PartValues;
import me.andpay.ti.xls.model.Row;
import me.andpay.ti.xls.model.Xls;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author echo.weng
 */
public class PoiXlsxWriter extends AbstractXlsWriter {
	// 样式进行缓存
	Map<List<Object>, XSSFCellStyle> xssfCellStyleCacheMap = new WeakHashMap<List<Object>, XSSFCellStyle>();

	public void write(OutputStream outputStream, PartValues values, String templePath) throws PoiXlsxException {
		XSSFWorkbook XSSFWorkbook = new XSSFWorkbook();

		write(XSSFWorkbook, outputStream, values, templePath);
	}

	public void write(String writeFilePath, PartValues values, String templePath) {
		XSSFWorkbook XSSFWorkbook = getXSSFWorkbook(writeFilePath);

		write(XSSFWorkbook, writeFilePath, values, templePath);
	}

	/**
	 * 插入 value可以为null
	 */
	public void writeInsert(String writeFilePath, Object value, String templePath, int rowIndex, int partIndex) {
		XSSFWorkbook XSSFWorkbook = getXSSFWorkbook(writeFilePath);

		// 获取xls模板
		Xls xls = getXls(templePath);

		List<Part> parts = xls.getParts();

		if (partIndex > parts.size() || partIndex < 0) {
			throw new PoiXlsxException("PartIndex is illegal");
		}
		Part currPart = parts.get(partIndex - 1);

		XSSFSheet XSSFSheet = getSheet(XSSFWorkbook, xls, false);

		// 设值
		setPartValue(currPart, value);

		int startRow = startRow(XSSFSheet, rowIndex, currPart, value);
		for (Row row : currPart.getRows()) {
			startRow = createRow(row, XSSFSheet, XSSFWorkbook, startRow);
		}

		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getNewFile(writeFilePath));
			XSSFWorkbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsxException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private void write(XSSFWorkbook workbook, PartValues values, String templePath) {
		// 将缓存中得cell样式清空掉，因为不同的sheet不能共用cell样式
		xssfCellStyleCacheMap.clear();

		// 获取xls模板
		Xls xls = getXls(templePath);

		XSSFSheet sheet = getSheet(workbook, xls, true);

		List<Part> parts = xls.getParts();
		int startRow = 0;
		int length = parts.size();

		for (int index = 0; index < length; index++) {
			Part part = parts.get(index);
			if (part.getStartIndex() > 0) {
				startRow = startRow + part.getStartIndex();
			}

			Object value = values.getValue(index);
			setPartValue(part, value);

			for (Row row : part.getRows()) {
				startRow = createRow(row, sheet, workbook, startRow);
			}
		}

		// 设置宽度
		setWidth(sheet, xls);
	}

	private void write(XSSFWorkbook workbook, OutputStream outputStream, PartValues values, String templePath)
			throws PoiXlsxException {
		write(workbook, values, templePath);
		try {
			workbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsxException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private void write(XSSFWorkbook workbook, String writeFilePath, PartValues values, String templePath)
			throws PoiXlsxException {
		write(workbook, values, templePath);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getNewFile(writeFilePath));
			workbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsxException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private XSSFSheet getSheet(XSSFWorkbook workbook, Xls xls, boolean newSheet) {
		if (!StringUtil.isEmpty(xls.getSheetName())) {
			XSSFSheet sheet = workbook.getSheet(xls.getSheetName());
			if (sheet != null) {
				if(newSheet == false){
					return sheet;
				}
				workbook.removeSheetAt(workbook.getSheetIndex(xls.getSheetName()));
			}
			return workbook.createSheet(xls.getSheetName());
		}

		if (workbook.getNumberOfSheets() > 0) {
			if(newSheet == false){
				return workbook.getSheetAt(0);
			}
			workbook.removeSheetAt(0);
		}
		return workbook.createSheet();
	}

	private int createRow(Row row, XSSFSheet sheet, XSSFWorkbook workbook, int startRow) {
		// 创建header
		XSSFRow XSSFRow = getRow(sheet, startRow);

		if (row.getHeight() > 0) {
			XSSFRow.setHeight((short) row.getHeight());

		} else if (row.getHeightInPoints() > 0) {
			XSSFRow.setHeightInPoints(row.getHeightInPoints());
		}
		
		List<Cell> cells = row.getCells();
		if(cells == null){
			return startRow + 1;
		}
		int length = cells.size();

		int currentIndex = 0;
		int maxRowSpan = 1;
		for (int index = 0; index < length; index++) {
			Cell cell = cells.get(index);
			if (StringUtil.isEmpty(cell.getIndex()) && (cell.getCellSpan() == null || cell.getCellSpan() <= 1)) {
				cell.setIndex(String.valueOf(index));
				createCell(currentIndex, cell, XSSFRow, workbook);
				maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
				currentIndex++;
				// 创建cell
			} else {
				if(cell.getCellSpan() != null){
					for (int i = 0; i < cell.getCellSpan(); i++) {
						if(i > 0){//如果不是第一次 则不设置值
							cell.setValue(null);
						}
						createCell(currentIndex + i, cell, XSSFRow, workbook);
					}
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
					currentIndex = currentIndex + cell.getCellSpan();
					continue;
				}
				// 解析index
				int[] indexs = parseIndex(cell.getIndex());
				if (indexs.length == 1) {
					currentIndex = indexs[0];
					createCell(currentIndex, cell, XSSFRow, workbook);
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
				} else {
					for (int i = indexs[0]; i < indexs[1]; i++) {
						if(i > indexs[0]){//如果不是第一次 则不设置值
							cell.setValue(null);
						}
						createCell(i, cell, XSSFRow, workbook);
					}
					// 合并单元格
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), indexs[0], indexs[1] - indexs[0] + 1, sheet, maxRowSpan);
					currentIndex = indexs[1] + 1;
				}
			}
		}

		return startRow + maxRowSpan;
	}
	
	private int mergeAndRtnMaxRowSpan(int startRow, Integer rowSpan, int startCell, Integer cellSpan, XSSFSheet sheet, int currentMaxRowSpan){
		if(rowSpan == null || rowSpan <= 1){
			rowSpan = 1;
		}
		if(cellSpan == null || cellSpan <= 1){
			cellSpan = 1;
		}
		if(rowSpan == 1 && cellSpan == 1){
			return currentMaxRowSpan;
		}
		if(rowSpan > 1){
			for (int rowIndex = startRow + 1; rowIndex < startRow + rowSpan; rowIndex++) {
				sheet.createRow(rowIndex);
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + rowSpan - 1, startCell, startCell + cellSpan - 1));
		//返回最大的rowSpan
		if(currentMaxRowSpan < rowSpan) return rowSpan;
		return currentMaxRowSpan;
	}

	private XSSFRow getRow(XSSFSheet sheet, int rowIndex) {
		XSSFRow XSSFRow = sheet.getRow(rowIndex);
		if (XSSFRow == null) {
			XSSFRow = sheet.createRow(rowIndex);
		}
		return XSSFRow;
	}

	private void createCell(int index, Cell cell, XSSFRow XSSFRow, XSSFWorkbook workbook) {
		XSSFCell XSSFCell = XSSFRow.createCell(index);

		XSSFCell.setCellStyle(createCellStyle(cell, workbook));
		if ("number".equals(cell.getType()) && !StringUtil.isEmpty(cell.getValue())) {
			XSSFCell.setCellValue(new BigDecimal(cell.getValue()).doubleValue());
			XSSFCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			DataFormat format = workbook.createDataFormat();
			if(StringUtil.isNotEmpty(cell.getFormate())){
				XSSFCell.getCellStyle().setDataFormat(format.getFormat(cell.getFormate()));
			}else{
				XSSFCell.getCellStyle().setDataFormat(format.getFormat("#,##0.00"));
			}
		} else {
			XSSFCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			XSSFCell.setCellValue(cell.getValue());
		}
	}

	private org.apache.poi.ss.usermodel.CellStyle createCellStyle(Cell cell, XSSFWorkbook workbook) {
		XSSFCellStyle cellStyle = xssfCellStyleCacheMap.get(Arrays.asList(cell.getCellStyle(), cell.getFont()));
		if (cellStyle != null) {
			return cellStyle;
		}

		cellStyle = workbook.createCellStyle();
		XSSFFont XSSFFont = workbook.createFont();
		Font font = cell.getFont();

		if (font != null) {
			if (!StringUtil.isEmpty(font.getFontName())) {
				XSSFFont.setFontName(font.getFontName());
			}

			if (font.getFontSize() > 0) {
				XSSFFont.setFontHeightInPoints((short) font.getFontSize());
			}

			if (font.getBoldweight() > 0) {
				XSSFFont.setBoldweight((short) font.getBoldweight());

			} else if (font.isBold()) {
				XSSFFont.setBoldweight((short) 1000);
			}

			cellStyle.setFont(XSSFFont);
			// 只有隐射文件中内容存在\n的时候 wrapText为true，自动为true，意思是强制换行
			cellStyle.setWrapText(font.isWrapText());
		}

		if (cell.getCellStyle() != null) {
			setBorder(cellStyle, cell);

			cellStyle.setVerticalAlignment(getVerticalAlignment(cell));
			cellStyle.setAlignment(getVlignment(cell));
		}
		xssfCellStyleCacheMap.put(Arrays.asList(cell.getCellStyle(), cell.getFont()), cellStyle);
		return cellStyle;
	}

	private short getVerticalAlignment(Cell cell) {
		if ("middle".equals(cell.getCellStyle().getVertical())) {
			return XSSFCellStyle.VERTICAL_CENTER;
		}
		if ("bottom".equals(cell.getCellStyle().getVertical())) {
			return XSSFCellStyle.VERTICAL_BOTTOM;
		}
		if ("top".equals(cell.getCellStyle().getVertical())) {
			return XSSFCellStyle.VERTICAL_TOP;
		}
		return XSSFCellStyle.VERTICAL_JUSTIFY;
	}

	private short getVlignment(Cell cell) {
		if ("center".equals(cell.getCellStyle().getAlignment())) {
			return XSSFCellStyle.ALIGN_CENTER_SELECTION;
		}
		if ("right".equals(cell.getCellStyle().getAlignment())) {
			return XSSFCellStyle.ALIGN_RIGHT;
		}
		if ("left".equals(cell.getCellStyle().getAlignment())) {
			return XSSFCellStyle.ALIGN_LEFT;
		}
		return XSSFCellStyle.ALIGN_JUSTIFY;
	}

	private void setBorder(XSSFCellStyle cellStyle, Cell cell) {
		String borderStr = cell.getCellStyle().getBorder();
		if (StringUtil.isEmpty(borderStr)) {
			return;
		}

		String[] borders = StringUtil.split(borderStr, ",");
		if (borders.length != 4) {
			throw new PoiXlsxException("Border is illegal");
		}
		// 上右下左的顺序
		if ("1".equals(borders[0])) {
			cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[1])) {
			cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[2])) {
			cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[3])) {
			cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		}
	}

	private void setWidth(XSSFSheet sheet, Xls xls) {
		if (StringUtil.isEmpty(xls.getCellWidth())) {
			int cellNum = getMaxCellNum(xls);
			while (cellNum-- > 0) {
				sheet.autoSizeColumn((short) cellNum, true); // 宽度自适应
			}
			return;
		}

		String[] cellWidths = xls.getCellWidth().split(",");
		for (int index = 0; index < cellWidths.length; index++) {
			sheet.setColumnWidth(index, Integer.parseInt(cellWidths[index]) * 100);
		}
	}

	private int getMaxCellNum(Xls xls) {
		int maxCellNum = 0;
		for (Part part : xls.getParts()) {
			List<Row> rows = part.getRows();
			if (CollectionUtil.isEmpty(rows)) {
				continue;
			}
			int cellNum = rows.get(0).getCells().size();
			if (maxCellNum < cellNum) {
				maxCellNum = cellNum;
			}
		}

		return maxCellNum;
	}

	private XSSFWorkbook getXSSFWorkbook(String filePath) throws PoiXlsxException {
		File file = new File(filePath);
		if (file.exists() == false || file.length() == 0) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			return workbook;
		}

		try {
			return new XSSFWorkbook(new FileInputStream(file));
		} catch (Exception e) {
			throw new PoiXlsxException(e);
		}
	}

	private int startRow(XSSFSheet sheet, int row, Part part, Object value) {
		int endRowIndex = sheet.getLastRowNum();
		int startMoveRow = row;
		if (startMoveRow < 0) {
			startMoveRow = endRowIndex + startMoveRow + 1;
		}

		int shiftRow = part.getRows().size();
		if (isList(value)) {
			shiftRow = ((List<?>) value).size();
		}

		sheet.shiftRows(startMoveRow, endRowIndex, shiftRow, true, true);
		return startMoveRow;
	}

}
