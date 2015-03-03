package me.andpay.ti.xls.model;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * 行
 * 
 * @author echo.weng
 * @since 2014年10月31日
 * 
 */
@Root
public class Row implements Cloneable {

	@Attribute(required = false)
	private int height;

	@Attribute(required = false)
	private int heightInPoints;

	@ElementList(required = false)
	private List<Cell> cells;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeightInPoints() {
		return heightInPoints;
	}

	public void setHeightInPoints(int heightInPoints) {
		this.heightInPoints = heightInPoints;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}

	public Row clone() {
		Row row = new Row();
		row.height = this.height;
		row.heightInPoints = this.heightInPoints;
		
		if (cells == null)
			return row;

		List<Cell> newCells = new ArrayList<Cell>(cells.size());
		for (Cell cell : cells) {
			newCells.add(cell.clone());
		}
		row.cells = newCells;
		return row;
	}

}
