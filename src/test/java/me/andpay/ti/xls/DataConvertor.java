package me.andpay.ti.xls;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataConvertor {
	
	public static String converDate(Date date){
		if(date == null) return "";
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	
}
