package me.andpay.ti.xls.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class GlobalStyle {
	
	@Element(required = false)
	private Font font;
	
	@Element(required = false)
	private CellStyle cellStyle;

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
	
	
}
