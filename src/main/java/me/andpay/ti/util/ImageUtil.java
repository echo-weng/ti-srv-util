package me.andpay.ti.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * 图片工具类
 * 
 * @author alex
 */
public final class ImageUtil {
	/**
	 * 拼接多张图片并输出至指定文件，可指定清晰度
	 * 
	 * @param srcImageFiles
	 *            源图片文件集
	 * @param targetImageFile
	 *            合并后图片文件
	 * @param formatName
	 *            合并后图片格式, 为null则自动根据目标文件后缀名判断，如果是不支持的类型，则默认使用JPG格式处理
	 * @param imgNumPerLine
	 *            每行图片个数
	 * @param quality
	 *            压缩清晰度(0, 1] 建议为0.75F
	 */
	public static void splice(List<File> srcImageFiles, File targetImageFile, String formatName, int imgNumPerLine,
			float quality) {
		if (srcImageFiles == null || srcImageFiles.isEmpty()) {
			throw new IllegalArgumentException("Source image files cannot be empty");

		} else if (targetImageFile == null) {
			throw new IllegalArgumentException("Target image file cannot be null");

		} else if (imgNumPerLine <= 0) {
			throw new IllegalArgumentException("The number of image per line must be greater than zero");
		}

		if (StringUtil.isBlank(formatName)) {
			// 图片格式没设置，优先取目标文件后缀名
			formatName = FileUtil.getFileNameExtension(targetImageFile.getName());
		}

		// 横向/纵向图片个数
		int imgNumX = Math.min(srcImageFiles.size(), imgNumPerLine);
		int imgNumY = (int) Math.ceil((double) srcImageFiles.size() / imgNumX);

		// 单个图片最大尺寸
		int imgMaxWidth = 0, imgMaxHeight = 0;
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		for (File srcImageFile : srcImageFiles) {
			BufferedImage srcImage = null;
			try {
				srcImage = ImageIO.read(srcImageFile);
				srcImage.flush();
			} catch (IOException ie) {
				throw new RuntimeException("Read source image, filePath=" + srcImageFile.getAbsolutePath(), ie);
			}

			imgMaxWidth = Math.max(imgMaxWidth, (int) srcImage.getWidth(null));
			imgMaxHeight = Math.max(imgMaxHeight, (int) srcImage.getHeight(null));

			images.add(srcImage);
		}

		int maxWidth = imgMaxWidth * imgNumX;
		int maxHeight = imgMaxHeight * imgNumY;

		BufferedImage bufImg = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);

		// 填充底色 - 白色
		final int alphaColor = 0x00ffffff;
		for (int y = 0; y < maxHeight; y++) {
			for (int x = 0; x < maxWidth; x++) {
				bufImg.setRGB(x, y, alphaColor);
			}
		}

		// 拼接完整图片
		int offsetX = 0, offsetY = 0, imgIdx = 0;
		for (BufferedImage image : images) {
			int imgWidth = (int) image.getWidth(null);
			int imgHeight = (int) image.getHeight(null);

			bufImg.getGraphics().drawImage(image, offsetX, offsetY, imgWidth, imgHeight, null);
			offsetX += imgMaxWidth;

			if (++imgIdx >= imgNumX) {
				// 换行
				offsetY += imgMaxHeight;
				offsetX = 0;
				imgIdx = 0;
			}
		}

		try {
			writeImageData(bufImg, new FileOutputStream(targetImageFile), formatName, quality);
		} catch (IOException e) {
			throw new RuntimeException("Splice images error", e);
		}
	}

	/**
	 * 等比例缩放图片并输出至指定文件，可指定清晰度
	 * 
	 * @param srcImageFile
	 *            源图片文件
	 * @param targetImageFile
	 *            压缩后的图片文件
	 * @param formatName
	 *            压缩后的图片格式, 为null则自动根据目标文件及源文件的文件后缀名判断，如果是不支持的类型，则默认使用JPG格式处理
	 * @param percentage
	 *            压缩百分比
	 * @param quality
	 *            压缩清晰度(0, 1] 建议为0.75F
	 */
	public static void compress(File srcImageFile, File targetImageFile, String formatName, float percentage,
			float quality) {
		int[] imgDims = getImageDim(srcImageFile);

		int newWidth = (int) (imgDims[0] * percentage);
		int newHeight = (int) (imgDims[1] * percentage);

		compress(srcImageFile, targetImageFile, formatName, newWidth, newHeight, quality);
	}

	/**
	 * 改变图片大小并输出至指定文件，可指定清晰度
	 * 
	 * @param srcImageFile
	 *            源图片文件
	 * @param targetImageFile
	 *            压缩后的图片文件
	 * @param formatName
	 *            压缩后的图片格式, 为null则自动根据目标文件及源文件的文件后缀名判断，如果是不支持的类型，则默认使用JPG格式处理
	 * @param width
	 *            新图片的宽度
	 * @param height
	 *            新图片的高度
	 * @param quality
	 *            压缩清晰度(0, 1] 建议为0.75F
	 * @See ImageFormatNames
	 * @See ImageQualities
	 */
	public static void compress(File srcImageFile, File targetImageFile, String formatName, int width, int height,
			float quality) {
		if (srcImageFile == null || srcImageFile.exists() == false) {
			throw new IllegalArgumentException("Source image file must be existed");

		} else if (targetImageFile == null) {
			throw new IllegalArgumentException("Target image file cannot be null");

		} else if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(String.format(
					"Width and height must be greater than zero, width=%d, height=%d", width, height));
		}

		if (StringUtil.isBlank(formatName)) {
			// 图片格式没设置，优先取目标文件后缀名
			formatName = FileUtil.getFileNameExtension(targetImageFile.getName());
		}

		if (StringUtil.isBlank(formatName)) {
			// 图片格式仍无法判断，则取源文件文件后缀名
			formatName = FileUtil.getFileNameExtension(srcImageFile.getName());
		}

		BufferedImage bufImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		try {
			bufImg.getGraphics().drawImage(ImageIO.read(srcImageFile), 0, 0, width, height, null);
			bufImg.flush();

			writeImageData(bufImg, new FileOutputStream(targetImageFile), formatName, quality);
		} catch (IOException e) {
			throw new RuntimeException("Compress image error, filePath=" + srcImageFile.getAbsolutePath(), e);
		}
	}

	/**
	 * 获取图片的尺寸
	 * 
	 * @param srcImageFile
	 * @return int[] { 宽度，高度 }
	 */
	public static int[] getImageDim(File srcImageFile) {
		BufferedImage srcImage = null;
		try {
			srcImage = ImageIO.read(srcImageFile);
			srcImage.flush();
		} catch (IOException ie) {
			throw new RuntimeException("Read source image, filePath=" + srcImageFile.getAbsolutePath(), ie);
		}

		return new int[] { (int) srcImage.getWidth(null), (int) srcImage.getHeight(null) };
	}

	/**
	 * 输出图片数据至指定输出流
	 * 
	 * @param bufImg
	 * @param out
	 * @param formatName
	 * @param quality
	 */
	private static void writeImageData(BufferedImage bufImg, OutputStream out, String formatName, float quality) {
		if (ImageFormatNames.isSupportedFormat(formatName) == false) {
			// 如果是不支持的文件格式，则默认使用JPG格式尝试处理
			formatName = ImageFormatNames.JPG;
		}

		if (ImageQualities.isValidQuality(quality) == false) {
			// 如果图片质量不合法，则默认使用高质量类型
			quality = ImageQualities.HIGH;
		}

		ImageWriter imgWriter = null;
		ImageOutputStream imgOut = null;
		try {
			ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(bufImg);
			imgWriter = ImageIO.getImageWriters(type, formatName).next();

			ImageWriteParam imgWriteParams = imgWriter.getDefaultWriteParam();
			if (imgWriteParams.canWriteProgressive()) {
				imgWriteParams.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
			}

			if (quality < 1F && imgWriteParams.canWriteCompressed()) {
				imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

				if (imgWriteParams.getCompressionType() == null) {
					String[] supportedCompressTypes = imgWriteParams.getCompressionTypes();
					imgWriteParams.setCompressionType(ArrayUtil.getElement(supportedCompressTypes, 0));
				}

				imgWriteParams.setCompressionQuality(quality);
			}

			imgOut = ImageIO.createImageOutputStream(out);
			imgWriter.setOutput(imgOut);

			imgWriter.write(null, new IIOImage(bufImg, null, null), imgWriteParams);

			imgOut.flush();
		} catch (Exception e) {
			throw new RuntimeException("Compress image error", e);

		} finally {
			try {
				if (imgWriter != null) {
					imgWriter.dispose();
				}
			} catch (Exception ex) {
			}

			try {
				if (imgOut != null) {
					imgOut.close();
				}
			} catch (Exception ie) {
			}
		}
	}
}
