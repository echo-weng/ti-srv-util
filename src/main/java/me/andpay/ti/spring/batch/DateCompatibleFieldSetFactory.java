package me.andpay.ti.spring.batch;

import java.text.DateFormat;
import java.text.NumberFormat;

import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;

/**
 * 日期兼容数据域集工厂类。
 * 
 * @author sea.bao
 */
public class DateCompatibleFieldSetFactory implements FieldSetFactory {

	private DateFormat dateFormat;

	private NumberFormat numberFormat;

	/**
	 * The {@link NumberFormat} to use for parsing numbers. If unset the default
	 * locale will be used.
	 * @param numberFormat the {@link NumberFormat} to use for number parsing
	 */
	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	/**
	 * The {@link DateFormat} to use for parsing numbers. If unset the default
	 * pattern is ISO standard <code>yyyy/MM/dd</code>.
	 * @param dateFormat the {@link DateFormat} to use for date parsing
	 */
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	public FieldSet create(String[] values, String[] names) {
		DateCompatibleFieldSet fieldSet = new DateCompatibleFieldSet(values, names);
		return enhance(fieldSet);
	}

	/**
	 * {@inheritDoc}
	 */
	public FieldSet create(String[] values) {
		DateCompatibleFieldSet fieldSet = new DateCompatibleFieldSet(values);
		return enhance(fieldSet);
	}

	private FieldSet enhance(DateCompatibleFieldSet fieldSet) {
		if (dateFormat!=null) {
			fieldSet.setDateFormat(dateFormat);
		}
		if (numberFormat!=null) {
			fieldSet.setNumberFormat(numberFormat);
		}	
		return fieldSet;
	}
}
