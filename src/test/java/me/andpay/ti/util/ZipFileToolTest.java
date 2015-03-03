package me.andpay.ti.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * 压缩文件工具类测试类。
 * 
 * @author sea.bao
 */
public class ZipFileToolTest {

	@Test
	public void test() throws Exception {
		String path = ZipFileToolTest.class.getResource("/test-data").getPath();
		File zipFile = new File(path + "/test.zip");
		File[] files = new File[2];
		files[0] = new File(path + "/a.txt");
		files[1] = new File(path + "/b.txt");

		ZipFileTool.zipFiles(zipFile, files);
		Assert.assertTrue(zipFile.length() <= (files[0].length() + files[1].length()) * 0.5);

		zipFile = new File(path + "/test2.zip");
		ZipFileTool.zipFiles(zipFile, files, "test", null, true);
		Assert.assertTrue(zipFile.length() <= (files[0].length() + files[1].length()) * 0.5);

		String spath = path + "/unzip-data";
		ZipFileTool.unzipFile(zipFile, spath, "123456", "test/a.txt");
		Assert.assertEquals(toString(files[0]), toString(spath + "/test/a.txt"));

		zipFile = new File(path + "/test3.zip");
		ZipFileTool.zipFiles(zipFile, files, null, "123456", true);

		ZipFileTool.unzipFile(zipFile, spath, "123456", null);
		Assert.assertEquals(toString(files[0]), toString(spath + "/a.txt"));
		Assert.assertEquals(toString(files[1]), toString(spath + "/b.txt"));
	}

	private String toString(String filePath) throws IOException {
		return IOUtil.toString(new FileInputStream(filePath));
	}

	private String toString(File file) throws IOException {
		return IOUtil.toString(new FileInputStream(file));
	}
}
