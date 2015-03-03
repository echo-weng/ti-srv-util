package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * 简单Bean映射器测试类。
 * 
 * @author sea.bao
 */
public class SimpleBeanMarshallerTest {
	public static class TestBean {
		private String cardNo;

		private BigDecimal amt;

		private java.util.Date txnTime;

		private Double riskScore;
		
		private String memo;

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public BigDecimal getAmt() {
			return amt;
		}

		public void setAmt(BigDecimal amt) {
			this.amt = amt;
		}

		public java.util.Date getTxnTime() {
			return txnTime;
		}

		public void setTxnTime(java.util.Date txnTime) {
			this.txnTime = txnTime;
		}

		public Double getRiskScore() {
			return riskScore;
		}

		public void setRiskScore(Double riskScore) {
			this.riskScore = riskScore;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}
	}
	
	public static class MyItem {
		private String cardNo;
		
		private java.util.Date time;

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public java.util.Date getTime() {
			return time;
		}

		public void setTime(java.util.Date time) {
			this.time = time;
		}
	}
	
	public static class MyList {
		private List<MyItem> items = new ArrayList<MyItem>();

		public List<MyItem> getItems() {
			return items;
		}

		public void setItems(List<MyItem> items) {
			this.items = items;
		}

	}

	@Test
	public void test() {
		// 测试数值映射
		assertEquals("", SimpleBeanMarshaller.marshallPropertyValue("null", null));
		assertTrue(null == SimpleBeanMarshaller.unmarshallPropertyValue("", String.class));
		java.util.Date date = new java.util.Date();
		date.setTime(123L);
		assertEquals("123", SimpleBeanMarshaller.marshallPropertyValue("date", date));
		assertEquals(date, SimpleBeanMarshaller.unmarshallPropertyValue("123", java.util.Date.class));
		assertEquals("1.23", SimpleBeanMarshaller.marshallPropertyValue("double", 1.23));
		assertEquals("1.23", SimpleBeanMarshaller.unmarshallPropertyValue("1.23", String.class));
		
		try {
			SimpleBeanMarshaller.marshallPropertyValue("illegal", "=,");
			fail("not to throw ex.");
		} catch(Exception e) {
		}

		// 测试对象映射为字符串
		TestBean testBean = new TestBean();
		String str = SimpleBeanMarshaller.marshall(testBean, null);
		System.out.println("str=[" + str + "].");
		assertEquals("amt=,cardNo=,memo=,riskScore=,txnTime=", str);

		Map<String, String> propMap = new HashMap<String, String>();
		propMap.put("cardNo", "c");
		propMap.put("riskScore", "rs");
		str = SimpleBeanMarshaller.marshall(testBean, propMap);
		assertEquals("amt=,c=,memo=,rs=,txnTime=", str);

		testBean.setAmt(new BigDecimal("123.00"));
		testBean.setTxnTime(date);
		testBean.setCardNo("1234567890");
		testBean.setRiskScore(3.589);
		str = SimpleBeanMarshaller.marshall(testBean, propMap);
		assertEquals("amt=123.00,c=1234567890,memo=,rs=3.589,txnTime=123", str);

		// 测试属性名称映射反转
		propMap = SimpleBeanMarshaller.revertPropMap(propMap);
		assertEquals(2, propMap.size());
		assertEquals("cardNo", propMap.get("c"));
		assertEquals("riskScore", propMap.get("rs"));

		propMap = SimpleBeanMarshaller.revertPropMap(propMap);
		propMap.put("amt", "c");
		try {
			SimpleBeanMarshaller.revertPropMap(propMap);
			fail("not to throw ex.");
		} catch (RuntimeException e) {
		}
		
		// 测试字符串反映射为对象
		propMap = new HashMap<String, String>();
		propMap.put("cardNo", "c");
		propMap.put("riskScore", "rs");
		testBean = SimpleBeanMarshaller.unmarshall("amt=123.00,c=1234567890,memo=,rs=3.589,txnTime=123,u=3", TestBean.class, propMap);
		assertEquals(new BigDecimal("123.00"), testBean.getAmt());
		assertEquals("1234567890", testBean.getCardNo());
		assertTrue(3.589==testBean.getRiskScore());
		assertTrue(123L == testBean.getTxnTime().getTime());
		assertNull(testBean.getMemo());
		
		MyList myList = new MyList();
		MyItem myItem = new MyItem();
		myItem.cardNo = "12312312312";
		myItem.time = new java.util.Date();
		myList.items.add(myItem);
		myList.items.add(myItem);
		
		JacksonSerializer ser = new JacksonSerializer(false);
		System.out.println("list=[" + new String(ser.serialize(myList)) + "].");
	}

}
