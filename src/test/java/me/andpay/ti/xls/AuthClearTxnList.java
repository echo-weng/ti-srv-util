package me.andpay.ti.xls;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 授权清算交易流水类。
 * 
 * @author sea.bao
 */
@Entity
public class AuthClearTxnList {
	/**
	 * 授权交易编号
	 */
	@Id
	@NotNull
	@Size(max = 16)
	private String authTxnId;

	/**
	 * 交易编号
	 */
	@NotNull
	@Size(max = 16)
	private String txnId;

	/**
	 * 授权交易类型
	 */
	@NotNull
	@Size(min = 4, max = 4)
	private String authTxnType;

	/**
	 * 关联授权交易编号
	 */
	@NotNull
	@Size(max = 16)
	private String relAuthTxnId;

	/**
	 * 原授权交易编号
	 */
	@Size(max = 16)
	private String origAuthTxnId;

	/**
	 * 原授权方交易时间
	 */
	private java.util.Date origAuthTxnTime;

	/**
	 * 原授权方结算时间
	 */
	private java.util.Date origAuthSettleTime;

	/**
	 * 原授权方参考号
	 */
	@Size(max = 16)
	private String origAuthRef;

	/**
	 * 原授权终端号
	 */
	@Size(max = 32)
	private String origAuthTerminalId;

	/**
	 * 原授权交易授权方终端交易跟踪号
	 */
	@Size(max = 16)
	private String origAuthTermTraceNo;

	/**
	 * 原授权交易授权方终端交易票据号
	 */
	@Size(max = 16)
	private String origAuthTermInvoiceNo;

	/**
	 * 原授权交易授权方终端批次号
	 */
	@Size(max = 16)
	private String origAuthTermBatchNo;

	/**
	 * 原授权金额
	 */
	@Digits(integer = 12, fraction = 2)
	private BigDecimal origAuthAmt;

	/**
	 * 卡类型
	 */
	@Size(min = 1, max = 1)
	private String cardType;

	/**
	 * 卡组织编号
	 */
	@Size(min = 2, max = 2)
	private String cardAssoc;

	/**
	 * 外卡组织编号
	 */
	@Size(min = 2, max = 2)
	private String foreignCardAssoc;

	/**
	 * 卡号（加密）
	 */
	@NotNull
	@Size(max = 64)
	private String encCardNo;

	/**
	 * 短卡号
	 */
	@Size(max = 16)
	private String shortCardNo;

	/**
	 * 有效期（加密）
	 */
	@Size(max = 32)
	private String encExpirationDate;

	/**
	 * 发卡机构
	 */
	@Size(min = 8, max = 8)
	private String issuerId;

	/**
	 * 服务输入模式
	 */
	@NotNull
	@Size(min = 3, max = 3)
	private String serviceEntryMode;

	/**
	 * 授权币别
	 */
	@NotNull
	@Size(min = 3, max = 3)
	private String authCur;

	/**
	 * 授权金额
	 */
	@NotNull
	@Digits(integer = 15, fraction = 2)
	private BigDecimal authAmt;

	/**
	 * 授权清分清算类型
	 */
	@Size(min = 1, max = 1)
	private String authClearSettleType;

	/**
	 * 授权结算币别
	 */
	@NotNull
	@Size(min = 3, max = 3)
	private String authSettleCur;

	/**
	 * 授权结算金额
	 */
	@NotNull
	@Digits(integer = 15, fraction = 2)
	private BigDecimal authSettleAmt;

	/**
	 * 授权结算金额汇率
	 */
	@NotNull
	@Digits(integer = 10, fraction = 6)
	private BigDecimal authSettleFxRate;

	/**
	 * 收单机构编号
	 */
	@NotNull
	@Size(min = 8, max = 8)
	private String acquirerId;

	/**
	 * 授权网络编号
	 */
	@NotNull
	@Size(max = 16)
	private String authNetId;

	/**
	 * 授权商户编号
	 */
	@NotNull
	@Size(max = 32)
	private String authMerchantId;

	/**
	 * 授权商户名称
	 */
	@Size(max = 64)
	private String authMerchantName;

	/**
	 * 授权商户mcc
	 */
	@Size(min = 4, max = 4)
	private String authMcc;

	/**
	 * 授权终端编号
	 */
	@Size(max = 32)
	private String authTerminalId;

	/**
	 * 本代本标志
	 */
	private boolean onUS;

	/**
	 * 授权交易的交易时间（本地时间）
	 */
	@NotNull
	private java.util.Date txnTime;

	/**
	 * 授权方交易时间
	 */
	private java.util.Date authTxnTime;

	/**
	 * 授权方结算时间
	 */
	private java.util.Date authSettleTime;

	/**
	 * 授权方结算日期
	 */
	@Column(columnDefinition = "date")
	private java.util.Date authSettleDate;

	/**
	 * 授权方结算日期
	 */
	@Column(columnDefinition = "date")
	private java.util.Date authSettleValueDate;

	/**
	 * 授权逻辑交易日期
	 */
	@NotNull
	@Column(columnDefinition = "date")
	private java.util.Date authLogicTxnDate;

	/**
	 * 授权方交易参考号
	 */
	@Size(max = 64)
	private String authRef;

	/**
	 * 授权方终端交易跟踪号
	 */
	@Size(max = 16)
	private String authTermTraceNo;

	/**
	 * 授权方终端交易票据号
	 */
	@Size(max = 16)
	private String authTermInvoiceNo;

	/**
	 * 授权方终端批次号
	 */
	@Size(max = 16)
	private String authTermBatchNo;

	/**
	 * 授权码
	 */
	@Size(max = 6)
	private String authCode;

	/**
	 * 授权交易手续费币别
	 */
	@NotNull
	@Size(min = 3, max = 3)
	private String authTxnFeeCur;

	/**
	 * 授权交易手续费
	 */
	@NotNull
	@Digits(integer = 12, fraction = 2)
	private BigDecimal authTxnFee;

	/**
	 * 授权交易返佣费
	 */
	@Digits(integer = 12, fraction = 2)
	private BigDecimal authTxnFeeCommission;

	/**
	 * 授权交易手续费率
	 */
	@Digits(integer = 10, fraction = 6)
	private BigDecimal authTxnFeeRate;

	/**
	 * 授权交易手续费返佣率
	 */
	@Digits(integer = 10, fraction = 6)
	private BigDecimal authTxnFeeCommissionRate;

	/**
	 * 授权交易手续费返还率
	 */
	@Digits(integer = 10, fraction = 6)
	private BigDecimal authTxnFeeRebateRate;

	/**
	 * 授权交易服务费币别
	 */
	@NotNull
	@Size(min = 3, max = 3)
	private String authSrvFeeCur;

	/**
	 * 授权交易服务费率
	 */
	@Digits(integer = 10, fraction = 6)
	private BigDecimal authSrvFeeRate;

	/**
	 * 授权交易服务费
	 */
	@NotNull
	@Digits(integer = 12, fraction = 2)
	private BigDecimal authSrvFee;

	/**
	 * 应用分区编号
	 */
	@NotNull
	@Size(min = 1, max = 1)
	private String appPartitionId;

	/**
	 * 授权清分批次编号
	 */
	private Integer authClearBatchId;

	/**
	 * 离线交易标志
	 */
	@NotNull
	@Size(min = 1, max = 1)
	private String offlineTxnFlag;

	/**
	 * 离线交易批次编号
	 */
	private Integer offlineTxnBatchId;

	/**
	 * 授权方结算标志
	 */
	@NotNull
	@Size(min = 1, max = 1)
	private String authSettleFlag = "1";

	/**
	 * 发卡机构主动发起交易标志
	 */
	private boolean iaiFlag;

	/**
	 * 原因代码
	 */
	@Size(max = 8)
	private String reasonCode;

	/**
	 * 错账标志
	 */
	@Size(min = 1, max = 1)
	private String errorFlag;

	public String getAuthTxnId() {
		return authTxnId;
	}

	public void setAuthTxnId(String authTxnId) {
		this.authTxnId = authTxnId;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getAuthTxnType() {
		return authTxnType;
	}

	public void setAuthTxnType(String authTxnType) {
		this.authTxnType = authTxnType;
	}

	public String getRelAuthTxnId() {
		return relAuthTxnId;
	}

	public void setRelAuthTxnId(String relAuthTxnId) {
		this.relAuthTxnId = relAuthTxnId;
	}

	public String getOrigAuthTxnId() {
		return origAuthTxnId;
	}

	public void setOrigAuthTxnId(String origAuthTxnId) {
		this.origAuthTxnId = origAuthTxnId;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardAssoc() {
		return cardAssoc;
	}

	public void setCardAssoc(String cardAssoc) {
		this.cardAssoc = cardAssoc;
	}

	public String getForeignCardAssoc() {
		return foreignCardAssoc;
	}

	public void setForeignCardAssoc(String foreignCardAssoc) {
		this.foreignCardAssoc = foreignCardAssoc;
	}

	public String getEncCardNo() {
		return encCardNo;
	}

	public void setEncCardNo(String encCardNo) {
		this.encCardNo = encCardNo;
	}

	public String getShortCardNo() {
		return shortCardNo;
	}

	public void setShortCardNo(String shortCardNo) {
		this.shortCardNo = shortCardNo;
	}

	public String getEncExpirationDate() {
		return encExpirationDate;
	}

	public void setEncExpirationDate(String encExpirationDate) {
		this.encExpirationDate = encExpirationDate;
	}

	public String getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(String issuerId) {
		this.issuerId = issuerId;
	}

	public String getServiceEntryMode() {
		return serviceEntryMode;
	}

	public void setServiceEntryMode(String serviceEntryMode) {
		this.serviceEntryMode = serviceEntryMode;
	}

	public String getAuthCur() {
		return authCur;
	}

	public void setAuthCur(String authCur) {
		this.authCur = authCur;
	}

	public BigDecimal getAuthAmt() {
		return authAmt;
	}

	public void setAuthAmt(BigDecimal authAmt) {
		this.authAmt = authAmt;
	}

	public String getAuthSettleCur() {
		return authSettleCur;
	}

	public void setAuthSettleCur(String authSettleCur) {
		this.authSettleCur = authSettleCur;
	}

	public BigDecimal getAuthSettleAmt() {
		return authSettleAmt;
	}

	public void setAuthSettleAmt(BigDecimal authSettleAmt) {
		this.authSettleAmt = authSettleAmt;
	}

	public BigDecimal getAuthSettleFxRate() {
		return authSettleFxRate;
	}

	public void setAuthSettleFxRate(BigDecimal authSettleFxRate) {
		this.authSettleFxRate = authSettleFxRate;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getAuthNetId() {
		return authNetId;
	}

	public void setAuthNetId(String authNetId) {
		this.authNetId = authNetId;
	}

	public String getAuthMerchantId() {
		return authMerchantId;
	}

	public void setAuthMerchantId(String authMerchantId) {
		this.authMerchantId = authMerchantId;
	}

	public String getAuthMcc() {
		return authMcc;
	}

	public void setAuthMcc(String authMcc) {
		this.authMcc = authMcc;
	}

	public String getAuthTerminalId() {
		return authTerminalId;
	}

	public void setAuthTerminalId(String authTerminalId) {
		this.authTerminalId = authTerminalId;
	}

	public boolean isOnUS() {
		return onUS;
	}

	public void setOnUS(boolean onUS) {
		this.onUS = onUS;
	}

	public java.util.Date getAuthTxnTime() {
		return authTxnTime;
	}

	public void setAuthTxnTime(java.util.Date authTxnTime) {
		this.authTxnTime = authTxnTime;
	}

	public String getAuthRef() {
		return authRef;
	}

	public void setAuthRef(String authRef) {
		this.authRef = authRef;
	}

	public String getAuthTermTraceNo() {
		return authTermTraceNo;
	}

	public void setAuthTermTraceNo(String authTermTraceNo) {
		this.authTermTraceNo = authTermTraceNo;
	}

	public String getAuthTermInvoiceNo() {
		return authTermInvoiceNo;
	}

	public void setAuthTermInvoiceNo(String authTermInvoiceNo) {
		this.authTermInvoiceNo = authTermInvoiceNo;
	}

	public String getAuthTermBatchNo() {
		return authTermBatchNo;
	}

	public void setAuthTermBatchNo(String authTermBatchNo) {
		this.authTermBatchNo = authTermBatchNo;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthTxnFeeCur() {
		return authTxnFeeCur;
	}

	public void setAuthTxnFeeCur(String authTxnFeeCur) {
		this.authTxnFeeCur = authTxnFeeCur;
	}

	public BigDecimal getAuthTxnFee() {
		return authTxnFee;
	}

	public void setAuthTxnFee(BigDecimal authTxnFee) {
		this.authTxnFee = authTxnFee;
	}

	public BigDecimal getAuthTxnFeeRate() {
		return authTxnFeeRate;
	}

	public void setAuthTxnFeeRate(BigDecimal authTxnFeeRate) {
		this.authTxnFeeRate = authTxnFeeRate;
	}

	public BigDecimal getAuthTxnFeeRebateRate() {
		return authTxnFeeRebateRate;
	}

	public void setAuthTxnFeeRebateRate(BigDecimal authTxnFeeRebateRate) {
		this.authTxnFeeRebateRate = authTxnFeeRebateRate;
	}

	public String getAuthSrvFeeCur() {
		return authSrvFeeCur;
	}

	public void setAuthSrvFeeCur(String authSrvFeeCur) {
		this.authSrvFeeCur = authSrvFeeCur;
	}

	public BigDecimal getAuthSrvFeeRate() {
		return authSrvFeeRate;
	}

	public void setAuthSrvFeeRate(BigDecimal authSrvFeeRate) {
		this.authSrvFeeRate = authSrvFeeRate;
	}

	public BigDecimal getAuthSrvFee() {
		return authSrvFee;
	}

	public void setAuthSrvFee(BigDecimal authSrvFee) {
		this.authSrvFee = authSrvFee;
	}

	public java.util.Date getAuthLogicTxnDate() {
		return authLogicTxnDate;
	}

	public void setAuthLogicTxnDate(java.util.Date authLogicTxnDate) {
		this.authLogicTxnDate = authLogicTxnDate;
	}

	public String getAppPartitionId() {
		return appPartitionId;
	}

	public void setAppPartitionId(String appPartitionId) {
		this.appPartitionId = appPartitionId;
	}

	public Integer getAuthClearBatchId() {
		return authClearBatchId;
	}

	public void setAuthClearBatchId(Integer authClearBatchId) {
		this.authClearBatchId = authClearBatchId;
	}

	public String getAuthClearSettleType() {
		return authClearSettleType;
	}

	public void setAuthClearSettleType(String authClearSettleType) {
		this.authClearSettleType = authClearSettleType;
	}

	public String getOfflineTxnFlag() {
		return offlineTxnFlag;
	}

	public void setOfflineTxnFlag(String offlineTxnFlag) {
		this.offlineTxnFlag = offlineTxnFlag;
	}

	public java.util.Date getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(java.util.Date txnTime) {
		this.txnTime = txnTime;
	}

	public java.util.Date getOrigAuthTxnTime() {
		return origAuthTxnTime;
	}

	public void setOrigAuthTxnTime(java.util.Date origAuthTxnTime) {
		this.origAuthTxnTime = origAuthTxnTime;
	}

	public String getOrigAuthRef() {
		return origAuthRef;
	}

	public void setOrigAuthRef(String origAuthRef) {
		this.origAuthRef = origAuthRef;
	}

	public java.util.Date getOrigAuthSettleTime() {
		return origAuthSettleTime;
	}

	public void setOrigAuthSettleTime(java.util.Date origAuthSettleTime) {
		this.origAuthSettleTime = origAuthSettleTime;
	}

	public java.util.Date getAuthSettleTime() {
		return authSettleTime;
	}

	public void setAuthSettleTime(java.util.Date authSettleTime) {
		this.authSettleTime = authSettleTime;
	}

	public Integer getOfflineTxnBatchId() {
		return offlineTxnBatchId;
	}

	public void setOfflineTxnBatchId(Integer offlineTxnBatchId) {
		this.offlineTxnBatchId = offlineTxnBatchId;
	}

	public String getAuthSettleFlag() {
		return authSettleFlag;
	}

	public void setAuthSettleFlag(String authSettleFlag) {
		this.authSettleFlag = authSettleFlag;
	}

	public String getAuthMerchantName() {
		return authMerchantName;
	}

	public void setAuthMerchantName(String authMerchantName) {
		this.authMerchantName = authMerchantName;
	}

	public String getOrigAuthTerminalId() {
		return origAuthTerminalId;
	}

	public void setOrigAuthTerminalId(String origAuthTerminalId) {
		this.origAuthTerminalId = origAuthTerminalId;
	}

	public String getOrigAuthTermTraceNo() {
		return origAuthTermTraceNo;
	}

	public void setOrigAuthTermTraceNo(String origAuthTermTraceNo) {
		this.origAuthTermTraceNo = origAuthTermTraceNo;
	}

	public String getOrigAuthTermInvoiceNo() {
		return origAuthTermInvoiceNo;
	}

	public void setOrigAuthTermInvoiceNo(String origAuthTermInvoiceNo) {
		this.origAuthTermInvoiceNo = origAuthTermInvoiceNo;
	}

	public String getOrigAuthTermBatchNo() {
		return origAuthTermBatchNo;
	}

	public void setOrigAuthTermBatchNo(String origAuthTermBatchNo) {
		this.origAuthTermBatchNo = origAuthTermBatchNo;
	}

	public java.util.Date getAuthSettleDate() {
		return authSettleDate;
	}

	public void setAuthSettleDate(java.util.Date authSettleDate) {
		this.authSettleDate = authSettleDate;
	}

	public String getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

	public java.util.Date getAuthSettleValueDate() {
		return authSettleValueDate;
	}

	public void setAuthSettleValueDate(java.util.Date authSettleValueDate) {
		this.authSettleValueDate = authSettleValueDate;
	}

	public BigDecimal getAuthTxnFeeCommission() {
		return authTxnFeeCommission;
	}

	public void setAuthTxnFeeCommission(BigDecimal authTxnFeeCommission) {
		this.authTxnFeeCommission = authTxnFeeCommission;
	}

	public BigDecimal getAuthTxnFeeCommissionRate() {
		return authTxnFeeCommissionRate;
	}

	public void setAuthTxnFeeCommissionRate(BigDecimal authTxnFeeCommissionRate) {
		this.authTxnFeeCommissionRate = authTxnFeeCommissionRate;
	}

	public BigDecimal getOrigAuthAmt() {
		return origAuthAmt;
	}

	public void setOrigAuthAmt(BigDecimal origAuthAmt) {
		this.origAuthAmt = origAuthAmt;
	}

	public boolean isIaiFlag() {
		return iaiFlag;
	}

	public void setIaiFlag(boolean iaiFlag) {
		this.iaiFlag = iaiFlag;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

}
