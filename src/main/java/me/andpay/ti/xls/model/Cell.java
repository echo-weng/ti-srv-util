package me.andpay.ti.xls.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 单元格
 * 
 * @author echo.weng
 * @since 2014年10月31日
 */
@Root
public class Cell {
	
	private String value = "";
	
	/**
	 * 值表达式
	 */
	@Attribute(required = false, name = "value")
	private String expValue = "";
	
	/**
	 * 表示表格的位置 3,5 表示3，4，5这三列的表格进行合并
	 */
	@Attribute(required = false)
	private String index;
	
	@Attribute(required = false)
	private Integer cellSpan;
	
	@Attribute(required = false)
	private Integer rowSpan;
	
	@Attribute(required = false)
	private int width;

	@Element(required = false)
	private Font font;
	
	@Element(required = false)
	private CellStyle cellStyle;
	
	/**
	 * string,number,date。默认是string类型
	 */
	@Attribute(required = false)
	private String type;
	
	@Attribute(required = false)
	private String formate;
	
	@Attribute(required = false)
	private String convert;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.expValue = value;
		this.value = value;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public CellStyle getCellStyle() {
		return cellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}
	
	public String getExpValue() {
		return expValue;
	}

	public void setExpValue(String expValue) {
		this.expValue = expValue;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormate() {
		return formate;
	}

	public void setFormate(String formate) {
		this.formate = formate;
	}

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}
	
	public Integer getCellSpan() {
		return cellSpan;
	}

	public void setCellSpan(Integer cellSpan) {
		this.cellSpan = cellSpan;
	}
	
	public Integer getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(Integer rowSpan) {
		this.rowSpan = rowSpan;
	}

	public Cell clone() {
		Cell cell = new Cell();
		if(this.font != null){
			cell.font = this.font.clone();
		}
		if(this.cellStyle != null){
			cell.cellStyle = this.cellStyle.clone();
		}
		
		cell.index = this.index;
		cell.value = this.value;
		cell.width = this.width;
		cell.expValue = this.expValue;
		cell.convert = this.convert;
		cell.type = this.type;
		cell.formate = this.formate;
		cell.cellSpan = this.cellSpan;
		cell.rowSpan = this.rowSpan;
		
		return cell;
	}
	
}
