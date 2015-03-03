package me.andpay.ti.xls.helper;

import java.util.List;

/**
 * XLS行数据转换器接口
 * 
 * @author alex
 */
public interface XlsRowConverter<T> {
	/**
	 * 将每行的数据转换为指定对象
	 * 
	 * @param cells
	 * @param ctx
	 * @return
	 */
	T convert(List<String> cells, XlsReadContext ctx);
}
