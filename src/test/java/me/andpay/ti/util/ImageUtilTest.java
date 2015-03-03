package me.andpay.ti.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

/**
 * 图片工具测试类
 * 
 * @author alex
 */
public class ImageUtilTest {

	@Test
	public void testGetImageDimension() throws Exception {
		final int[] imgDim = new int[] { 800, 600 };
		Assert.assertArrayEquals(imgDim, ImageUtil.getImageDim(new File("src/test/resources/pic/test.jpg")));
		Assert.assertArrayEquals(imgDim, ImageUtil.getImageDim(new File("src/test/resources/pic/test.bmp")));
		Assert.assertArrayEquals(imgDim, ImageUtil.getImageDim(new File("src/test/resources/pic/test.gif")));
		Assert.assertArrayEquals(imgDim, ImageUtil.getImageDim(new File("src/test/resources/pic/test.png")));
	}

	@Test
	public void testSplice() throws Exception {
		final int imgTotalNum = 9;
		final int imgNumPerLine = 3;

		String fileName = "test.jpg";
		File srcImgFile = new File("src/test/resources/pic/" + fileName);
		int[] origDim = ImageUtil.getImageDim(srcImgFile);

		List<File> srcImgFiles = new ArrayList<File>();
		for (int i = 0; i < imgTotalNum; i++) {
			srcImgFiles.add(srcImgFile);
		}

		File newImgFile = FileUtil.createNewFile("target/pic/splice_" + fileName);

		// 拼接图片
		ImageUtil.splice(srcImgFiles, newImgFile, ImageFormatNames.JPG, imgNumPerLine, ImageQualities.LOW);

		int[] newDim = ImageUtil.getImageDim(newImgFile);

		Assert.assertEquals(origDim[0] * imgNumPerLine, newDim[0]);
		Assert.assertEquals(origDim[1] * (int) Math.ceil((double) imgTotalNum / imgNumPerLine), newDim[1]);
	}

	@Test
	public void testCompress() throws Exception {
		String fileName = "test.jpg";
		File srcFile = new File("src/test/resources/pic/" + fileName);

		File comFile = FileUtil.createNewFile("target/pic/com_" + fileName);
		File lowComFile = FileUtil.createNewFile("target/pic/lowCom_" + fileName);

		try {
			// 压缩大小(等比例)
			ImageUtil.compress(srcFile, comFile, ImageFormatNames.JPG, 0.5F, ImageQualities.RAW);
			Assert.assertTrue(comFile.length() < srcFile.length());

			// 压缩大小(等比例)与图片质量，根据后缀名(目标图->源图->JPG)判断压缩图片类型
			ImageUtil.compress(srcFile, lowComFile, null, 0.5F, ImageQualities.LOW); // 根据后缀名判断
			Assert.assertTrue(lowComFile.length() < comFile.length());

			fileName = "test.bmp";
			srcFile = new File("src/test/resources/pic/" + fileName);

			// 转换图片格式
			ImageUtil.compress(srcFile, comFile, ImageFormatNames.GIF, 1F, ImageQualities.RAW);
			Assert.assertTrue(comFile.length() < srcFile.length());

			ImageUtil.compress(srcFile, comFile, ImageFormatNames.PNG, 1F, ImageQualities.LOW);
			Assert.assertTrue(comFile.length() < srcFile.length());

			ImageUtil.compress(srcFile, comFile, ImageFormatNames.BMP, 1F, ImageQualities.LOW); // 无法压缩
			Assert.assertTrue(comFile.length() == srcFile.length());

			// 改变图片大小并压缩图片质量
			ImageUtil.compress(srcFile, comFile, ImageFormatNames.JPG, 200, 100, ImageQualities.LOW);
			Assert.assertTrue(comFile.length() < srcFile.length());

			ImageUtil.compress(srcFile, comFile, ImageFormatNames.PNG, 200, 100, ImageQualities.LOW);
			Assert.assertTrue(comFile.length() < srcFile.length());

			ImageUtil.compress(srcFile, comFile, ImageFormatNames.BMP, 200, 100, ImageQualities.LOW); // 无法压缩
			Assert.assertTrue(comFile.length() < srcFile.length());

		} finally {
			comFile.delete();
			lowComFile.delete();
		}
	}
}
