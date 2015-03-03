package me.andpay.ti.xls.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 部分
 * 
 * @author echo.weng
 * @since 2014年10月31日
 *
 */
@Root
public class Part {
	
	@Attribute(required = false)
	private int startIndex;
	
	@Element(name = "global", required =false)
	private GlobalStyle globalStyle;
	
	@ElementList
	private List<Row> rows;

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public GlobalStyle getGlobalStyle() {
		return globalStyle;
	}

	public void setGlobalStyle(GlobalStyle globalStyle) {
		this.globalStyle = globalStyle;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}
	
}
