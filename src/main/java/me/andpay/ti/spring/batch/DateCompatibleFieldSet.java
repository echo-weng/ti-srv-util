package me.andpay.ti.spring.batch;

import java.util.Date;

import org.springframework.batch.item.file.transform.DefaultFieldSet;

/**
 * 日期兼容性数据域集类。
 * 
 * @author sea.bao
 */
public class DateCompatibleFieldSet extends DefaultFieldSet {

	public DateCompatibleFieldSet(String[] tokens) {
		super(tokens);
	}

	public DateCompatibleFieldSet(String[] tokens, String[] names) {
		super(tokens, names);
	}

	@Override
	public Date readDate(int index) {
		try {
			return super.readDate(index);
		} catch(IllegalArgumentException e) {
			return super.readDate(index, "yyyy-MM-dd HH:mm:ss:SSS");
		}
	}

}
