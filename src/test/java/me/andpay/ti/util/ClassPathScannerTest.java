package me.andpay.ti.util;

import static org.junit.Assert.*;
import java.util.Set;

import org.junit.Test;

/**
 * 类路径扫描器测试类。
 * 
 * @author sea.bao
 */
public class ClassPathScannerTest {

	@Test
	public void test() {
		Set<Class<?>> classes = ClassPathScanner.scanForClassesByAnnotations("me.andpay.ti.util", TestAnnotation.class);
		for ( Class<?> clazz : classes ) {
			System.out.println(clazz);
		}
		
		assertEquals(2, classes.size());
	}

}
