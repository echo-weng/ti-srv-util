package me.andpay.ti.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 定时Ifttt类测试类。
 * 
 * @author sea.bao
 */
public class TimingIftttTest {
	private int count = 0;

	private String checkResult = "a";
	
	private String lock = "1";

	@Test
	public void test() throws Throwable {
		TimingIfttt<String> ifttt = new TimingIfttt<String>();
		ifttt.register("r0", 1000, new TimingIfttt.This<String>() {

			public String check() {
				return checkResult;
			}
		}, new TimingIfttt.That<String>() {

			public void doIt(String result) {
				synchronized(lock) {
					count++;
				}
				
				System.out.println("Done r0.");
			}
		});

		Thread.sleep(1500);
		assertEquals(1, count);
		Thread.sleep(1000);
		assertEquals(2, count);
		checkResult = null;
		Thread.sleep(1500);
		assertEquals(2, count);

		ifttt.register("r1", 1000, new TimingIfttt.This<String>() {

			public String check() {
				return checkResult;
			}
		}, new TimingIfttt.That<String>() {

			public void doIt(String checkResult) {
				synchronized(lock) {
					count++;
				}
				
				System.out.println("Done r1.");
			}
		});
		
		count = 0;
		checkResult = "a";
		Thread.sleep(1500);
		assertEquals(2, count);
		
		ifttt.unregister("r0");
		ifttt.unregister("r1");
		
		Thread.sleep(1500);
		assertEquals(2, count);
	}

}
