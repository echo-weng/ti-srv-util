package me.andpay.ti.util;

import org.junit.Test;

/**
 * 字符串相似度匹配器测试类。
 * 
 * @author sea.bao
 */
public class LevenshteinWordMatcherTest {

	@Test
	public void test() {
		System.out.println(LevenshteinWordMatcher.match("中国工商银行上海分行", "中国工商银行上海分行"));
		System.out.println(LevenshteinWordMatcher.match("中国工商银行上海分行", "工商银行上海分行"));
		System.out.println(LevenshteinWordMatcher.match("中国工商银行", "工商银行上海分行"));
		System.out.println(LevenshteinWordMatcher.match("(香港地区)三井住友银行香港分行", "三井住友银行香港分行"));
	}

}
