package me.andpay.ti.xls.helper;

/**
 * XLS行读取回调接口
 * 
 * @author alex
 */
public interface XlsReadRowCallback<T> {
	/**
	 * 读取行数据
	 * 
	 * @param rowObj
	 * @param ctx
	 */
	void readRow(T rowObj, XlsReadContext ctx);
}
