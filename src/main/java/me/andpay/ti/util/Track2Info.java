package me.andpay.ti.util;

/**
 * 2磁道信息类。
 * 
 * @author sea.bao
 */
public class Track2Info {
	/**
	 * 账号
	 */
	private String pan;
	
	/**
	 * 有效期
	 */
	private String expiryDate;
	
	/**
	 * 服务代码
	 */
	private String serviceCode;
	
	/**
	 * 任意数据
	 */
	private String discretionaryData;

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getDiscretionaryData() {
		return discretionaryData;
	}

	public void setDiscretionaryData(String discretionaryData) {
		this.discretionaryData = discretionaryData;
	}
}
