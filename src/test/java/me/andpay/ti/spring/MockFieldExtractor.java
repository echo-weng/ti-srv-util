package me.andpay.ti.spring;

import java.util.List;

import me.andpay.ti.spring.batch.AccumulateFieldExtractor;


public class MockFieldExtractor implements AccumulateFieldExtractor<Object> {

	public Object[] extract(Object item) {
		return (Object[] )item;
	}

	public List<Object[]> extractLast() {
		return null;
	}

}
