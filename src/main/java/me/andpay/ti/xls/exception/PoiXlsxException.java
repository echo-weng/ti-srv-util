package me.andpay.ti.xls.exception;

/**
 * xls异常类
 * 
 * @author echo.weng
 * @since 2014年11月7日
 */
public class PoiXlsxException extends ExcelException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PoiXlsxException(String msg){
		super(msg);
	}
	
	public PoiXlsxException(Throwable e){
		super(e);
	}
	
	public PoiXlsxException(String msg, Throwable e){
		super(msg, e);
	}

}
