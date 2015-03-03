package me.andpay.ti.xls.exception;

/**
 * Excel异常类
 * 
 * @author echo.weng
 * @since 2014年11月7日
 */
public class ExcelException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExcelException(String msg){
		super(msg);
	}
	
	public ExcelException(Throwable e){
		super(e);
	}
	
	public ExcelException(String msg, Throwable e){
		super(msg, e);
	}

}

