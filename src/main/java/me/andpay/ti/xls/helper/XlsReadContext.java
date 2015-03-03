package me.andpay.ti.xls.helper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Xls读取上下文
 * 
 * @author alex
 */
public class XlsReadContext {
	/**
	 * 工作表名称
	 */
	private String sheetName;

	/**
	 * 工作表下标
	 */
	private int sheetIndex;

	/**
	 * 工作表总数
	 */
	private int sheetCount;

	/**
	 * 当前工作表的当前行号
	 */
	private int rowNum;

	/**
	 * 当前工作表的总行数
	 */
	private int rowCount;

	/**
	 * 当前行的单元格总数
	 */
	private int cellCount;

	/**
	 * 扩展参数
	 */
	private Map<String, Object> attrs;

	/**
	 * @return the sheetName
	 */
	public String getSheetName() {
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            the sheetName to set
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	/**
	 * @return the sheetIndex
	 */
	public int getSheetIndex() {
		return sheetIndex;
	}

	/**
	 * @param sheetIndex
	 *            the sheetIndex to set
	 */
	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	/**
	 * @return the sheetCount
	 */
	public int getSheetCount() {
		return sheetCount;
	}

	/**
	 * @param sheetCount
	 *            the sheetCount to set
	 */
	public void setSheetCount(int sheetCount) {
		this.sheetCount = sheetCount;
	}

	/**
	 * @return the rowNum
	 */
	public int getRowNum() {
		return rowNum;
	}

	/**
	 * @param rowNum
	 *            the rowNum to set
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount
	 *            the rowCount to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @return the cellCount
	 */
	public int getCellCount() {
		return cellCount;
	}

	/**
	 * @param cellCount
	 *            the cellCount to set
	 */
	public void setCellCount(int cellCount) {
		this.cellCount = cellCount;
	}

	/**
	 * @return the attrs
	 */
	public Map<String, Object> getAttrs() {
		return attrs;
	}

	/**
	 * @param attrs
	 *            the attrs to set
	 */
	public void setAttrs(Map<String, Object> attrs) {
		this.attrs = attrs;
	}

	/**
	 * 添加参数
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public Object setAttr(String name, Object value) {
		if (attrs == null) {
			attrs = new LinkedHashMap<String, Object>();
		}

		return attrs.put(name, value);
	}

	/**
	 * 获取参数
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttr(String name) {
		return (attrs != null ? attrs.get(name) : null);
	}
}
