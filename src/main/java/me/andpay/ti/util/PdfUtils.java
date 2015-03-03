package me.andpay.ti.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * PDF 工具类
 * 
 * @author tinyliu
 * 
 */
public class PdfUtils {

	private static float DEFAULT_MARGIN_SIZE = 50;

	/**
	 * 将图片转换为PDF文件
	 * 
	 * @param pdfFilePath
	 *            生成的PDF文件路径和名称
	 * @param imageFiles
	 *            图片信息
	 * @param rectangle
	 *            PDF单页大小，图片会根据单页大小进行伸缩
	 */
	public static void generatePdf(String pdfFilePath, List<File> imageFiles, Rectangle rectangle) {
		Document document = new Document(rectangle, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE, DEFAULT_MARGIN_SIZE,
				DEFAULT_MARGIN_SIZE);

		try {
			PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();

			for (File imageFile : imageFiles) {
				Image image = Image.getInstance(imageFile.getPath());
				image.scaleToFit(rectangle.getWidth() - 2 * DEFAULT_MARGIN_SIZE, rectangle.getHeight() - 2
						* DEFAULT_MARGIN_SIZE);
				document.add(image);
			}

			document.close();
		} catch (Exception e) {
			throw new RuntimeException("generate Pdf file error, filePath=" + pdfFilePath, e);
		}
	}

	// public static void main(String[] args) {
	// File testDir = new
	// File("/Users/tinyliu/Desktop/1015960000004046_20140820060000/apos");
	// PdfUtils.generatePdf("/Users/tinyliu/Desktop/1015960000004046_20140820060000/test.pdf",
	// ArrayUtil.asList(testDir.listFiles()), PageSize.A4);
	// }
}
