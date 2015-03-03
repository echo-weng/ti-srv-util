package me.andpay.ti.xls.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * 字体样式
 * 
 * @author echo.weng
 * @since 2014年10月31日
 */
@Root
public class Font {

	@Attribute(required = false)
	private String fontName;

	@Attribute(required = false)
	private float fontSize;

	@Attribute(required = false)
	private int boldweight;

	@Attribute(required = false)
	private boolean bold;

	@Attribute(required = false)
	private String color;

	@Attribute(required = false)
	private Boolean italic;

	@Attribute(required = false)
	private Boolean strikeout;

	@Attribute(required = false)
	private Boolean underline;

	private boolean wrapText;

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public int getBoldweight() {
		return boldweight;
	}

	public void setBoldweight(int boldweight) {
		this.boldweight = boldweight;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(Boolean italic) {
		this.italic = italic;
	}

	public Boolean isStrikeout() {
		return strikeout;
	}

	public void setStrikeout(Boolean strikeout) {
		this.strikeout = strikeout;
	}

	public Boolean isUnderline() {
		return underline;
	}

	public void setUnderline(Boolean underline) {
		this.underline = underline;
	}

	public boolean isWrapText() {
		return wrapText;
	}

	public void setWrapText(boolean wrapText) {
		this.wrapText = wrapText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bold ? 1231 : 1237);
		result = prime * result + boldweight;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
		result = prime * result + Float.floatToIntBits(fontSize);
		result = prime * result + ((italic == null) ? 0 : italic.hashCode());
		result = prime * result + ((strikeout == null) ? 0 : strikeout.hashCode());
		result = prime * result + ((underline == null) ? 0 : underline.hashCode());
		result = prime * result + (wrapText ? 1231 : 1237);
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
		Font other = (Font) obj;
		if (bold != other.bold)
			return false;
		if (boldweight != other.boldweight)
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (fontName == null) {
			if (other.fontName != null)
				return false;
		} else if (!fontName.equals(other.fontName))
			return false;
		if (Float.floatToIntBits(fontSize) != Float.floatToIntBits(other.fontSize))
			return false;
		if (italic == null) {
			if (other.italic != null)
				return false;
		} else if (!italic.equals(other.italic))
			return false;
		if (strikeout == null) {
			if (other.strikeout != null)
				return false;
		} else if (!strikeout.equals(other.strikeout))
			return false;
		if (underline == null) {
			if (other.underline != null)
				return false;
		} else if (!underline.equals(other.underline))
			return false;
		if (wrapText != other.wrapText)
			return false;
		return true;
	}

	public Font clone() {
		Font font = new Font();
		font.boldweight = this.boldweight;
		font.bold = bold;
		font.color = this.color;
		font.fontName = this.fontName;
		font.fontSize = this.fontSize;
		font.italic = this.italic;
		font.strikeout = this.strikeout;
		font.underline = this.underline;
		return font;
	}
}
