package me.andpay.ti.util;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

/**
 * Jar类工具类测试类。
 * 
 * @author sea.bao
 */
public class JarClassUtilTest {

	@Test
	public void test() throws IOException {
		String[] jars = { "./test-jar/ac-txn-api-1.0.0-SNAPSHOT.jar", "./test-jar/ti-daf-1.0.0-SNAPSHOT.jar" };
		Set<Class<?>> classes = JarClassUtil.loadClassesFromJars(jars);
		for ( Class<?> clazz : classes ) {
			System.out.println(clazz);
		}
	}

}
