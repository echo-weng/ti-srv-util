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
import me.andpay.ti.xls.exception.PoiXlsException;
import me.andpay.ti.xls.impl.AbstractXlsWriter;
import me.andpay.ti.xls.model.Cell;
import me.andpay.ti.xls.model.Font;
import me.andpay.ti.xls.model.Part;
import me.andpay.ti.xls.model.PartValues;
import me.andpay.ti.xls.model.Row;
import me.andpay.ti.xls.model.Xls;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author echo.weng
 */
public class PoiXlsWriter extends AbstractXlsWriter {
	// 样式进行缓存
	Map<List<Object>, HSSFCellStyle> hssfCellStyleCacheMap = new WeakHashMap<List<Object>, HSSFCellStyle>();

	public void write(OutputStream outputStream, PartValues values, String templePath) throws PoiXlsException {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();

		write(hssfWorkbook, outputStream, values, templePath);
	}

	public void write(String writeFilePath, PartValues values, String templePath) {
		HSSFWorkbook hssfWorkbook = getHSSFWorkbook(writeFilePath);

		write(hssfWorkbook, writeFilePath, values, templePath);
	}

	/**
	 * 插入 value可以为null
	 */
	public void writeInsert(String writeFilePath, Object value, String templePath, int rowIndex, int partIndex) {
		HSSFWorkbook hssfWorkbook = getHSSFWorkbook(writeFilePath);

		// 获取xls模板
		Xls xls = getXls(templePath);

		List<Part> parts = xls.getParts();

		if (partIndex > parts.size() || partIndex < 0) {
			throw new PoiXlsException("PartIndex is illegal");
		}
		Part currPart = parts.get(partIndex - 1);

		HSSFSheet hssfSheet = getSheet(hssfWorkbook, xls);

		// 设值
		setPartValue(currPart, value);

		int startRow = startRow(hssfSheet, rowIndex, currPart, value);
		for (Row row : currPart.getRows()) {
			startRow = createRow(row, hssfSheet, hssfWorkbook, startRow);
		}

		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getNewFile(writeFilePath));
			hssfWorkbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private void write(HSSFWorkbook workbook, PartValues values, String templePath) {
		// 将缓存中得cell样式清空掉，因为不同的sheet不能共用cell样式
		hssfCellStyleCacheMap.clear();

		// 获取xls模板
		Xls xls = getXls(templePath);

		HSSFSheet sheet = getSheet(workbook, xls);

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

	private void write(HSSFWorkbook workbook, OutputStream outputStream, PartValues values, String templePath)
			throws PoiXlsException {
		write(workbook, values, templePath);
		try {
			workbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private void write(HSSFWorkbook workbook, String writeFilePath, PartValues values, String templePath)
			throws PoiXlsException {
		write(workbook, values, templePath);
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getNewFile(writeFilePath));
			workbook.write(outputStream);
		} catch (Exception e) {
			throw new PoiXlsException(e);
		} finally {
			CloseUtil.close(outputStream);
		}
	}

	private HSSFSheet getSheet(HSSFWorkbook workbook, Xls xls) {
		if (!StringUtil.isEmpty(xls.getSheetName())) {
			HSSFSheet sheet = workbook.getSheet(xls.getSheetName());
			if (sheet != null) {
				return sheet;
			}

			return workbook.createSheet(xls.getSheetName());
		}

		if (workbook.getNumberOfSheets() > 0) {
			return workbook.getSheetAt(0);
		}

		return workbook.createSheet();
	}

	private int createRow(Row row, HSSFSheet sheet, HSSFWorkbook workbook, int startRow) {
		// 创建header
		HSSFRow hssfRow = getRow(sheet, startRow);

		if (row.getHeight() > 0) {
			hssfRow.setHeight((short) row.getHeight());

		} else if (row.getHeightInPoints() > 0) {
			hssfRow.setHeightInPoints(row.getHeightInPoints());
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
				createCell(currentIndex, cell, hssfRow, workbook);
				maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
				currentIndex++;
				// 创建cell
			} else {
				if(cell.getCellSpan() != null){
					for (int i = 0; i < cell.getCellSpan(); i++) {
						if(i > 0){//如果不是第一次 则不设置值
							cell.setValue(null);
						}
						createCell(currentIndex + i, cell, hssfRow, workbook);
					}
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
					currentIndex = currentIndex + cell.getCellSpan();
					continue;
				}
				// 解析index
				int[] indexs = parseIndex(cell.getIndex());
				if (indexs.length == 1) {
					currentIndex = indexs[0];
					createCell(currentIndex, cell, hssfRow, workbook);
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), currentIndex, cell.getCellSpan(), sheet, maxRowSpan);
				} else {
					for (int i = indexs[0]; i < indexs[1]; i++) {
						if(i > indexs[0]){//如果不是第一次 则不设置值
							cell.setValue(null);
						}
						createCell(i, cell, hssfRow, workbook);
					}
					// 合并单元格
					maxRowSpan = mergeAndRtnMaxRowSpan(startRow, cell.getRowSpan(), indexs[0], indexs[1] - indexs[0] + 1, sheet, maxRowSpan);
					currentIndex = indexs[1] + 1;
				}
			}
		}

		return startRow + maxRowSpan;
	}
	
	private int mergeAndRtnMaxRowSpan(int startRow, Integer rowSpan, int startCell, Integer cellSpan, HSSFSheet sheet, int currentMaxRowSpan){
		if(rowSpan == null || rowSpan <= 1){
			rowSpan = 1;
		}
		if(cellSpan == null || cellSpan <= 1){
			cellSpan = 1;
		}
		if(rowSpan == 1 && cellSpan == 1){
			return currentMaxRowSpan;
		}
		sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + rowSpan - 1, startCell, startCell + cellSpan - 1));
		//返回最大的rowSpan
		if(currentMaxRowSpan < rowSpan) return rowSpan;
		return currentMaxRowSpan;
	}

	private HSSFRow getRow(HSSFSheet sheet, int rowIndex) {
		HSSFRow hssfRow = sheet.getRow(rowIndex);
		if (hssfRow == null) {
			hssfRow = sheet.createRow(rowIndex);
		}
		return hssfRow;
	}

	private void createCell(int index, Cell cell, HSSFRow hssfRow, HSSFWorkbook workbook) {
		HSSFCell hssfCell = hssfRow.createCell(index);
		
		hssfCell.setCellStyle(createCellStyle(cell, workbook));
		if ("number".equals(cell.getType()) && !StringUtil.isEmpty(cell.getValue())) {
			hssfCell.setCellValue(new BigDecimal(cell.getValue()).doubleValue());
			hssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC);
			DataFormat format = workbook.createDataFormat();
			if(StringUtil.isNotEmpty(cell.getFormate())){
				hssfCell.getCellStyle().setDataFormat(format.getFormat(cell.getFormate()));
			}else{
				hssfCell.getCellStyle().setDataFormat(format.getFormat("#,##0.00"));
			}
		} else {
			hssfCell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
			hssfCell.setCellValue(cell.getValue());
		}
	}

	private org.apache.poi.ss.usermodel.CellStyle createCellStyle(Cell cell, HSSFWorkbook workbook) {
		HSSFCellStyle cellStyle = hssfCellStyleCacheMap.get(Arrays.asList(cell.getCellStyle(), cell.getFont()));
		if (cellStyle != null) {
			return cellStyle;
		}

		cellStyle = workbook.createCellStyle();
		HSSFFont hssfFont = workbook.createFont();
		Font font = cell.getFont();

		if (font != null) {
			if (!StringUtil.isEmpty(font.getFontName())) {
				hssfFont.setFontName(font.getFontName());
			}

			if (font.getFontSize() > 0) {
				hssfFont.setFontHeightInPoints((short) font.getFontSize());
			}

			if (font.getBoldweight() > 0) {
				hssfFont.setBoldweight((short) font.getBoldweight());

			} else if (font.isBold()) {
				hssfFont.setBoldweight((short) 1000);
			}

			cellStyle.setFont(hssfFont);
			// 只有隐射文件中内容存在\n的时候 wrapText为true，自动为true，意思是强制换行
			cellStyle.setWrapText(font.isWrapText());
		}

		if (cell.getCellStyle() != null) {
			setBorder(cellStyle, cell);

			cellStyle.setVerticalAlignment(getVerticalAlignment(cell));
			cellStyle.setAlignment(getVlignment(cell));
		}
		hssfCellStyleCacheMap.put(Arrays.asList(cell.getCellStyle(), cell.getFont()), cellStyle);
		return cellStyle;
	}

	private short getVerticalAlignment(Cell cell) {
		if ("middle".equals(cell.getCellStyle().getVertical())) {
			return HSSFCellStyle.VERTICAL_CENTER;
		}
		if ("bottom".equals(cell.getCellStyle().getVertical())) {
			return HSSFCellStyle.VERTICAL_BOTTOM;
		}
		if ("top".equals(cell.getCellStyle().getVertical())) {
			return HSSFCellStyle.VERTICAL_TOP;
		}
		return HSSFCellStyle.VERTICAL_JUSTIFY;
	}

	private short getVlignment(Cell cell) {
		if ("center".equals(cell.getCellStyle().getAlignment())) {
			return HSSFCellStyle.ALIGN_CENTER_SELECTION;
		}
		if ("right".equals(cell.getCellStyle().getAlignment())) {
			return HSSFCellStyle.ALIGN_RIGHT;
		}
		if ("left".equals(cell.getCellStyle().getAlignment())) {
			return HSSFCellStyle.ALIGN_LEFT;
		}
		return HSSFCellStyle.ALIGN_JUSTIFY;
	}

	private void setBorder(HSSFCellStyle cellStyle, Cell cell) {
		String borderStr = cell.getCellStyle().getBorder();
		if (StringUtil.isEmpty(borderStr)) {
			return;
		}

		String[] borders = StringUtil.split(borderStr, ",");
		if (borders.length != 4) {
			throw new PoiXlsException("Border is illegal");
		}
		// 上右下左的顺序
		if ("1".equals(borders[0])) {
			cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[1])) {
			cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[2])) {
			cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		}
		if ("1".equals(borders[3])) {
			cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		}
	}

	private void setWidth(HSSFSheet sheet, Xls xls) {
		if (StringUtil.isEmpty(xls.getCellWidth())) {
			int cellNum = getMaxCellNum(xls);
			while (cellNum-- >= 0) {
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

	private HSSFWorkbook getHSSFWorkbook(String filePath) throws PoiXlsException {
		File file = new File(filePath);
		if (file.exists() == false || file.length() == 0) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			return workbook;
		}

		try {
			return new HSSFWorkbook(new FileInputStream(file));
		} catch (Exception e) {
			throw new PoiXlsException(e);
		}
	}

	private int startRow(HSSFSheet sheet, int row, Part part, Object value) {
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
