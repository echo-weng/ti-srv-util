package me.andpay.ti.xls.model;

/**
 * xls 每一部分的值对象 
 * @author echo.weng
 */
public class PartValue {
	
	private int index;
	
	private Object value;
	
	public static PartValue newInstance(int index, Object value){
		return new PartValue(index, value);
	}
	
	public PartValue(int index, Object value){
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
