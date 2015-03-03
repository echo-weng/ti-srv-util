package me.andpay.ti.xls;

import java.util.List;

import junit.framework.Assert;
import me.andpay.ti.util.JSON;
import me.andpay.ti.xls.helper.XlsReadContext;
import me.andpay.ti.xls.helper.XlsRowConverter;
import me.andpay.ti.xls.helper.XlsReadRowCallback;
import me.andpay.ti.xls.impl.poi.PoiXlsReader;

import org.junit.Test;

/**
 * Excel文件读取器测试
 * 
 * @author alex
 */
public class PoiXlsReaderTest {

	@Test
	public void testRead() throws Exception {
		PoiXlsReader reader = new PoiXlsReader();
		XlsRowConverter<List<String>> converter = new XlsRowConverter<List<String>>() {
			public List<String> convert(List<String> cells, XlsReadContext ctx) {
				return cells;
			}
		};

		XlsReadRowCallback<List<String>> callback = new XlsReadRowCallback<List<String>>() {
			public void readRow(List<String> rowObj, XlsReadContext ctx) {
				Assert.assertEquals(3, ctx.getSheetCount());

				switch (ctx.getSheetIndex()) {
					case 0:
						Assert.assertEquals("工作表1", ctx.getSheetName());
						Assert.assertEquals(8, ctx.getRowCount());

						switch (ctx.getRowNum()) {
							case 0:
								Assert.assertEquals(4, rowObj.size());
								Assert.assertNull(rowObj.get(0));
								Assert.assertNull(rowObj.get(1));
								Assert.assertNull(rowObj.get(2));
								Assert.assertEquals("abc测试", rowObj.get(3));
								break;

							case 2:
								Assert.assertEquals(2, rowObj.size());
								Assert.assertEquals("2.1", rowObj.get(0));
								Assert.assertEquals("3.0", rowObj.get(1));
								break;

							case 4:
								Assert.assertEquals(4, rowObj.size());
								Assert.assertNull(rowObj.get(0));
								Assert.assertEquals("5.1", rowObj.get(1));
								Assert.assertEquals("#NAME?", rowObj.get(2));
								Assert.assertEquals("abc测试", rowObj.get(3));
								break;

							case 6:
								Assert.assertEquals(5, rowObj.size());
								Assert.assertEquals("2015-01-18 00:00:00", rowObj.get(0));
								Assert.assertNull(rowObj.get(1));
								Assert.assertNull(rowObj.get(2));
								Assert.assertEquals("2015-01-18 23:30:00", rowObj.get(3));
								Assert.assertNull(rowObj.get(4));
								break;

							case 7:
								Assert.assertEquals(6, rowObj.size());
								Assert.assertNull(rowObj.get(0));
								Assert.assertNull(rowObj.get(1));
								Assert.assertEquals("true", rowObj.get(2));
								Assert.assertNull(rowObj.get(3));
								Assert.assertNull(rowObj.get(4));
								Assert.assertEquals("false", rowObj.get(5));
								break;

							default:
								Assert.fail("Unexpected row, sheetIdx=" + ctx.getSheetIndex() + ", rowNum="
										+ ctx.getRowNum());
						}
						break;

					case 2:
						Assert.assertEquals("工作表3", ctx.getSheetName());
						Assert.assertEquals(4, ctx.getRowCount());

						switch (ctx.getRowNum()) {
							case 3:
								Assert.assertEquals(3, rowObj.size());
								Assert.assertNull(rowObj.get(0));
								Assert.assertNull(rowObj.get(1));
								Assert.assertEquals("1.0", rowObj.get(2));
								break;

							default:
								Assert.fail("Unexpected row, sheetIdx=" + ctx.getSheetIndex() + ", rowNum="
										+ ctx.getRowNum());

						}
						break;

					default:
						Assert.fail("Unexpected sheet, sheetIdx=" + ctx.getSheetIndex() + ", sheetName="
								+ ctx.getSheetName());
				}

				System.out.printf("sheetIdx=%d/%d, rowNum=%d/%d, cellCount=%d, obj=%s%n", ctx.getSheetIndex(), ctx
						.getSheetCount(), ctx.getRowNum(), ctx.getRowCount(), ctx.getCellCount(), JSON.getDefault()
						.toJSONString(rowObj));
			}
		};

		// xls
		reader.read("classpath:test-data/PoiXlsReaderTest.xls", converter, callback);

		// xlsx
		reader.read("classpath:test-data/PoiXlsReaderTest.xlsx", converter, callback);
	}
}
