package me.andpay.ti.util;

import junit.framework.Assert;

import org.junit.Test;

public class IdCardUtilTest {

	@Test
	public void test() {
		String idCard = "430211198510120456";
		Assert.assertEquals(30, IdCardUtil.getAgeByIdCard(idCard));
		Assert.assertEquals("19851012", IdCardUtil.getBirthByIdCard(idCard));
		Assert.assertEquals(IdCardUtil.GENDER_MAN, IdCardUtil.getGenderByIdCard(idCard));
		Assert.assertEquals(12, IdCardUtil.getDateByIdCard(idCard));
		Assert.assertEquals(10, IdCardUtil.getMonthByIdCard(idCard));
	}

}
