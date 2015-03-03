package me.andpay.ti.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 磁道工具测试类。
 * 
 * @author sea.bao
 */
public class TrackUtilTest {

	@Test
	public void test() {
		Track2Info t2Info = TrackUtil.parseTrack2("1234567890123456A0101A123");
		assertEquals("1234567890123456", t2Info.getPan());
		assertEquals("0101", t2Info.getExpiryDate());
		assertNull(t2Info.getServiceCode());
		assertEquals("123", t2Info.getDiscretionaryData());
		
		String track2 = TrackUtil.formatTrack2(t2Info);
		System.out.println("track2=" + track2);
		
		t2Info = TrackUtil.parseTrack2("1234567890123456AAA123");
		assertEquals("1234567890123456", t2Info.getPan());
		assertNull(t2Info.getExpiryDate());
		assertNull(t2Info.getServiceCode());
		assertEquals("123", t2Info.getDiscretionaryData());
		
		track2 = TrackUtil.formatTrack2(t2Info);
		System.out.println("track2=" + track2);
		
		String track1 = "B4392250016716042^LIAO HUAZAI              ^150310119887    999900735000000 ";
		Track1Info t1Info = TrackUtil.parseTrack1(track1);
		assertEquals("4392250016716042", t1Info.getPan());
		assertEquals("LIAO HUAZAI", t1Info.getName());
		assertEquals("1503", t1Info.getExpiryDate());
		assertEquals("101", t1Info.getServiceCode());
		assertEquals("19887    999900735000000 ", t1Info.getDiscretionaryData());
		
		

		String track2_ic1 = "6212261001011139000=23082204719991348";
		String track2_ic2 = "6259073198008900=18112010000013912340";
		
		assertFalse(TrackUtil.isICCardByTrack2(track2));
		assertTrue(TrackUtil.isICCardByTrack2(track2_ic1));
		assertTrue(TrackUtil.isICCardByTrack2(track2_ic2));
		
		assertFalse(TrackUtil.isICCardByTrack2(null));
		assertFalse(TrackUtil.isICCardByTrack2("6259073198008900"));
		assertFalse(TrackUtil.isICCardByTrack2("6259073198008900=1811"));
	}

}
