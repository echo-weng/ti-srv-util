package me.andpay.ti.xls.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author echo.weng
 */
public class PartValues {
	
	private List<PartValue> partValues;
	
	public void addPartValue(int index, Object value){
		if(partValues == null){
			partValues = new ArrayList<PartValue>();
		}
		partValues.add(PartValue.newInstance(index, value));
	}
	
	public Object getValue(int index){
		if(partValues == null){
			return null;
		}
		
		for(PartValue partValue : partValues){
			if(partValue.getIndex() == index){
				return partValue.getValue();
			}
		}
		return null;
	}

	public List<PartValue> getPartValues() {
		return partValues;
	}

}
