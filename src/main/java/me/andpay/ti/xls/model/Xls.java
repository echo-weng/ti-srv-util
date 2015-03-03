package me.andpay.ti.xls.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "xls")
public class Xls {

	@Element(name = "global", required = false)
	private GlobalStyle globalStyle;

	@ElementList(name = "parts")
	private List<Part> parts;

	@Attribute(name = "cellWidth", required = false)
	private String cellWidth;
	
	@Attribute(name = "sheet", required = false)
	private String sheetName;

	public GlobalStyle getGlobalStyle() {
		return globalStyle;
	}

	public void setGlobalStyle(GlobalStyle globalStyle) {
		this.globalStyle = globalStyle;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}

	public String getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(String cellWidth) {
		this.cellWidth = cellWidth;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}
