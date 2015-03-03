package me.andpay.ti.spring;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 模拟读出器类。
 * 
 * @author sea.bao
 * 
 */
public class MockItemReader implements ItemReader<Object> {
	private int c=0;
	
	public int count = 20;
	
	public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if ( c >= count ) {
			return null;
		} else {
			c++;
			return new Object[]{c, "abc"};
		}
	}

}
