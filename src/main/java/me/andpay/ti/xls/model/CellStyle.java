package me.andpay.ti.xls.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * 表格样式
 * 
 * @author echo.weng
 * @since 2014年10月31日
 */
@Root
public class CellStyle {
	
	@Attribute(required = false)
	private String alignment;
	
	@Attribute(required = false)
	private String vertical;
	
	@Attribute(required = false)
	private String border;
	
	@Attribute(required = false)
	private String backgroundColor;

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public String getVertical() {
		return vertical;
	}

	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alignment == null) ? 0 : alignment.hashCode());
		result = prime * result + ((backgroundColor == null) ? 0 : backgroundColor.hashCode());
		result = prime * result + ((border == null) ? 0 : border.hashCode());
		result = prime * result + ((vertical == null) ? 0 : vertical.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CellStyle other = (CellStyle) obj;
		if (alignment == null) {
			if (other.alignment != null)
				return false;
		} else if (!alignment.equals(other.alignment))
			return false;
		if (backgroundColor == null) {
			if (other.backgroundColor != null)
				return false;
		} else if (!backgroundColor.equals(other.backgroundColor))
			return false;
		if (border == null) {
			if (other.border != null)
				return false;
		} else if (!border.equals(other.border))
			return false;
		if (vertical == null) {
			if (other.vertical != null)
				return false;
		} else if (!vertical.equals(other.vertical))
			return false;
		return true;
	}

	public CellStyle clone() {
		CellStyle cellStyle = new CellStyle();
		cellStyle.alignment = this.alignment;
		cellStyle.backgroundColor = this.backgroundColor;
		cellStyle.border = this.border;
		cellStyle.vertical = this.vertical;
		return cellStyle;
	}
	
}
