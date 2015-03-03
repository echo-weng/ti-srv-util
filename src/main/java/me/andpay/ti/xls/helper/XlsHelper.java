package me.andpay.ti.xls.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import me.andpay.ti.util.IOUtil;
import me.andpay.ti.util.StringUtil;
import me.andpay.ti.xls.model.Cell;
import me.andpay.ti.xls.model.CellStyle;
import me.andpay.ti.xls.model.Font;
import me.andpay.ti.xls.model.GlobalStyle;
import me.andpay.ti.xls.model.Part;
import me.andpay.ti.xls.model.Row;
import me.andpay.ti.xls.model.Xls;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

/**
 * xls辅助类
 * 
 * @author echo.weng
 */
public class XlsHelper {

	/**
	 * 序列化工具
	 */
	public static Serializer serializer = new Persister(new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));

	public static Xls parse(String xmlStr) throws Exception {
		return serializer.read(Xls.class, xmlStr, false);
	}

	/**
	 * 设置样式 如果局部样式不存在，则用全局样式进行设置
	 * 
	 * @param xls
	 * @return
	 */
	public static Xls convertTo(Xls xls) {
		GlobalStyle globalStyle = xls.getGlobalStyle();
		List<Part> parts = xls.getParts();
		setPartsStyle(parts, globalStyle);

		return xls;
	}

	public static String getTemple(String templePath) throws Exception {
		if (templePath.startsWith("classpath:")) {
			templePath = templePath.substring("classpath:".length());
			return IOUtil.toString(XlsHelper.class.getClassLoader().getResourceAsStream(templePath));
		}

		return IOUtil.toString(new FileInputStream(new File(templePath)));
	}

	private static void setPartsStyle(List<Part> parts, GlobalStyle globalStyle) {
		for (Part part : parts) {
			setPartStyle(part, globalStyle);
		}
	}

	private static void setPartStyle(Part part, GlobalStyle globalStyle) {
		GlobalStyle partGlobalStyle = part.getGlobalStyle();
		List<Row> rows = part.getRows();
		setRowsStyle(rows, partGlobalStyle, globalStyle);
	}

	/**
	 * 设置行样式
	 * 
	 * @param rows
	 * @param partGlobalStyle
	 * @param globalStyle
	 */
	private static void setRowsStyle(List<Row> rows, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		for (Row row : rows) {
			setRowStyle(row, partGlobalStyle, globalStyle);
		}
	}

	private static void setRowStyle(Row row, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		List<Cell> cells = row.getCells();
		if(cells == null){
			return;
		}
		setCellsStyle(cells, partGlobalStyle, globalStyle);
	}

	private static void setCellsStyle(List<Cell> cells, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		for (Cell cell : cells) {
			setCellStyle(cell, partGlobalStyle, globalStyle);
		}
	}

	private static void setCellStyle(Cell cell, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		mergeCellStyle(cell, partGlobalStyle, globalStyle);
		mergeFont(cell, partGlobalStyle, globalStyle);
	}

	private static void mergeCellStyle(Cell cell, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		mergeCellStyle(cell, partGlobalStyle);

		mergeCellStyle(cell, globalStyle);
	}

	private static void mergeCellStyle(Cell cell, GlobalStyle globalStyle) {
		if (globalStyle == null)
			return;

		if (cell.getCellStyle() == null) {
			cell.setCellStyle(globalStyle.getCellStyle());
			return;
		}

		CellStyle first = cell.getCellStyle();
		CellStyle second = globalStyle.getCellStyle();

		if (StringUtil.isEmpty(first.getAlignment())) {
			first.setAlignment(second.getAlignment());
		}
		if (StringUtil.isEmpty(first.getBackgroundColor())) {
			first.setBackgroundColor(second.getBackgroundColor());
		}
		if (StringUtil.isEmpty(first.getBorder())) {
			first.setBorder(second.getBorder());
		}
		if (StringUtil.isEmpty(first.getVertical())) {
			first.setVertical(second.getVertical());
		}
	}

	private static void mergeFont(Cell cell, GlobalStyle partGlobalStyle, GlobalStyle globalStyle) {
		mergeFont(cell, partGlobalStyle);

		mergeFont(cell, globalStyle);
	}

	private static void mergeFont(Cell cell, GlobalStyle globalStyle) {
		if (globalStyle == null || globalStyle.getFont() == null)
			return;

		if (cell.getFont() == null) {
			cell.setFont(globalStyle.getFont());
			return;
		}

		Font first = cell.getFont();
		Font second = globalStyle.getFont();

		if (first.getBoldweight() == 0) {
			first.setBoldweight(second.getBoldweight());
		}

		if (first.isBold() == false) {
			first.setBold(second.isBold());
		}

		if (StringUtil.isEmpty(first.getColor())) {
			first.setColor(second.getColor());
		}
		if (StringUtil.isEmpty(first.getFontName())) {
			first.setFontName(second.getFontName());
		}
		if (first.getFontSize() == 0) {
			first.setFontSize(second.getFontSize());
		}
	}

}
