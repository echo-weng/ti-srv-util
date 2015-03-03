package me.andpay.ti.xls;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.andpay.ti.xls.impl.poi.PoiXlsWriter;
import me.andpay.ti.xls.impl.poi.PoiXlsxWriter;
import me.andpay.ti.xls.model.PartValues;

import org.junit.Test;

public class PoiXlsWirterTest {
	
	@Test
	public void testPoiXlsWriter() throws Exception{
		PoiXlsWriter poiXlsWirter = new PoiXlsWriter();
		PartValues partValues = new PartValues();
		partValues.addPartValue(1, createAuthClearTxnList());
		partValues.addPartValue(2, createAuthClearTxnList());
		
		poiXlsWirter.write("/Users/install/Documents/和付3.xls", partValues, "classpath:me/andpay/ti/xls/xls-refund.xml");
	}
	
	@Test
	public void testPoiXlsWriter1() throws Exception{
		PoiXlsWriter poiXlsWirter = new PoiXlsWriter();
		PartValues partValues = new PartValues();
		partValues.addPartValue(1, createAuthClearTxnList());
		
		poiXlsWirter.write("/Users/install/Documents/和付3.xls", partValues, "classpath:me/andpay/ti/xls/ManualReconAuthTxnListExport.xml");
	}
	
	@Test
	public void testPoiXlsxWriter1() throws Exception{
		PoiXlsxWriter poiXlsWirter = new PoiXlsxWriter();
		PartValues partValues = new PartValues();
		partValues.addPartValue(1, createAuthClearTxnList());
		
		poiXlsWirter.write("/Users/install/Documents/和付3.xlsx", partValues, "classpath:me/andpay/ti/xls/ManualReconAuthTxnListExport.xml");
	}
	
//	@Test TODO 修复异常
	public void testPoiXlsWriterInsert() throws Exception{
		PoiXlsWriter poiXlsWirter = new PoiXlsWriter();
		List<Object> values = new ArrayList<Object>();
		values.add(createAuthClearTxnList());
		values.add(createAuthClearTxnList());
		
		poiXlsWirter.writeInsert("/Users/install/Documents/和付1.xls", values, "classpath:me/andpay/ti/xls/xls-refund.xml", -3, 3);
	}
	
	private static AuthClearTxnList createAuthClearTxnList(){
		AuthClearTxnList txnList = new AuthClearTxnList();
		txnList.setAuthTxnId("000000000853");
		txnList.setTxnId("000000001002");
		txnList.setAuthTxnType("0500");
		txnList.setRelAuthTxnId("000000000852");
		txnList.setOrigAuthTxnId("000000000852");
		txnList.setOrigAuthTxnTime(new Date());
		txnList.setOrigAuthRef("000000000063");
		txnList.setOrigAuthTerminalId("102310052710001");
		txnList.setOrigAuthTermTraceNo("000294");
		txnList.setOrigAuthTermBatchNo("000000");
		txnList.setEncCardNo("005B5231373734F118DE39B6958BBB4E311322CC8EF7D2317E");
		txnList.setShortCardNo("6225884342");
		txnList.setEncExpirationDate("00D35DDBCB47974BCE");
		txnList.setServiceEntryMode("021");
		txnList.setCardType("2");
		txnList.setCardAssoc("UP");
		txnList.setForeignCardAssoc("UP");
		txnList.setIssuerId("03080000");
		txnList.setAuthCur("CNY");
		txnList.setAuthAmt(new BigDecimal("-81"));
		txnList.setAuthClearSettleType("0");
		txnList.setAuthSettleValueDate(new Date());
		txnList.setAuthSettleFlag("0");
		txnList.setAuthSettleCur("CNY");
		txnList.setAuthSettleAmt(new BigDecimal("-79.20"));
		txnList.setAuthSettleFxRate(new BigDecimal("1.000000"));
		txnList.setAcquirerId("00010000");
		txnList.setAuthNetId("001");
		txnList.setAuthMerchantId("102310052710001");
		txnList.setAuthMerchantName("和付");
		txnList.setAuthMcc("7011");
		txnList.setAuthTerminalId("00000001");
		txnList.setOnUS(false);
		txnList.setTxnTime(new Date());
		txnList.setAuthLogicTxnDate(new Date());
		txnList.setAuthTxnTime(new Date());
		txnList.setAuthRef("000000000065");
		txnList.setAuthTermTraceNo("000296");
		txnList.setAuthTermBatchNo("000000");
		txnList.setAuthCode("000001");
		txnList.setAuthTxnFeeCur("CNY");
		txnList.setAuthTxnFee(new BigDecimal("0.010000"));
		txnList.setAuthSrvFeeCur("CNY");
		txnList.setAuthSrvFee(new BigDecimal("0.00"));
		txnList.setAuthSrvFeeRate(new BigDecimal("0.000000"));
		txnList.setAppPartitionId("0");
		txnList.setAuthClearBatchId(17);
		txnList.setOfflineTxnFlag("0");
		return txnList;
	}
}
