package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import me.andpay.ti.util.FileUtil.FileReadHandler;

import org.junit.Test;

/**
 * 可变更文件加载器类测试类。
 * 
 * @author sea.bao
 */
public class ModifiedFileLoaderTest {
	private int loadCount = 0;

	@Test
	public void test() throws Exception {
		String targetFileName = "/tmp/modified-file";
		File tfile = new File(targetFileName);
		FileUtil.writeFile(tfile, "123");
		
		FileLoader<String> loader = new FileLoader<String>() {
			public String load(FileReadHandler handler) throws IOException {
				loadCount++;
				return handler.getReader().readLine();
			}
		};
		
		long sTime = System.currentTimeMillis();
		GlobalModifiedFileLoader.registerFile(targetFileName, loader);
		long eTime = System.currentTimeMillis();
		
		System.out.println("first load elapsed=" + (eTime-sTime) + "ms.");
		assertEquals("123", GlobalModifiedFileLoader.load(targetFileName, String.class));
		assertEquals(1, loadCount);
		
		Thread.sleep(1500);
		assertEquals("123", GlobalModifiedFileLoader.load(targetFileName, String.class));
		assertEquals(1, loadCount);
		
		FileUtil.writeFile(tfile, "321");
		Thread.sleep(1500);
		assertEquals("321", GlobalModifiedFileLoader.load(targetFileName, String.class));
		assertEquals(2, loadCount);
		
		Thread.sleep(1500);
		assertEquals("321", GlobalModifiedFileLoader.load(targetFileName, String.class));
		assertEquals(2, loadCount);
		
		sTime = System.currentTimeMillis();
		for ( int i=0; i < 1000000; i++ ) {
			GlobalModifiedFileLoader.load(targetFileName, String.class);
		}
		eTime = System.currentTimeMillis();
		
		System.out.println("multi load elapsed=" + (eTime-sTime) + "ms.");
		
		GlobalModifiedFileLoader.unregisterFile(targetFileName);
		FileUtil.writeFile(tfile, "123");
		Thread.sleep(1500);
		try {
			GlobalModifiedFileLoader.load(targetFileName, String.class);
			fail("not to throw ex.");
		} catch(RuntimeException e) {
		}
		
		assertEquals(2, loadCount);
		
		tfile.delete();
		
		assertEquals(0, FileUtil.openingCount);
	}
}
