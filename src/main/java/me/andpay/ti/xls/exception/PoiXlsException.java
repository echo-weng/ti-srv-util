package me.andpay.ti.xls.exception;

/**
 * xls异常类
 * 
 * @author echo.weng
 * @since 2014年11月7日
 */
public class PoiXlsException extends ExcelException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PoiXlsException(String msg){
		super(msg);
	}
	
	public PoiXlsException(Throwable e){
		super(e);
	}
	
	public PoiXlsException(String msg, Throwable e){
		super(msg, e);
	}

}
