package me.andpay.ti.spring.batch;

import java.util.List;

import org.springframework.batch.item.file.transform.FieldExtractor;


/**
 * 积累型数据域抽取器接口定义类。
 * 
 * @author seabao
 *
 * @param <T>
 */
public interface AccumulateFieldExtractor<T> extends FieldExtractor<T> {
	/**
	 * 抽取最后的数据，数据尾调用
	 * @return
	 */
	List<Object[]> extractLast();
}
