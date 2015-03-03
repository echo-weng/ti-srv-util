package me.andpay.ti.comp;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * @author seabao
 * 
 */
public class ScratchTest {
	private static Map<BigDecimal, SortedSet<Integer>> loadExcel(InputStream in, int cellNum) throws Exception {
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(in);

		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		Map<BigDecimal, SortedSet<Integer>> m1 = new TreeMap<BigDecimal, SortedSet<Integer>>();

		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			HSSFCell cell = row.getCell(cellNum);
			if ( cell == null ) {
				continue;
			}
			
			BigDecimal amt;

			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (cell.getNumericCellValue() != 0.00) {
					amt = new BigDecimal(cell.getNumericCellValue());
				} else {
					continue;
				}
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				try {
					amt = new BigDecimal(cell.getStringCellValue());
				} catch (Exception e) {
					continue;
				}
			} else {
				continue;
			}

			amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);

			SortedSet<Integer> rows = m1.get(amt);
			if (rows == null) {
				rows = new TreeSet<Integer>();
				m1.put(amt, rows);
			}

			rows.add(i);
		}

		return m1;
	}

	private static void markExcel(InputStream in, String outFile, int cellNum, Map<BigDecimal, SortedSet<Integer>> m)
			throws Exception {
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(in);

		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		for (Map.Entry<BigDecimal, SortedSet<Integer>> entry : m.entrySet()) {
			for (Integer rowIndex : entry.getValue()) {
				HSSFRow row = sheet.getRow(rowIndex);
				HSSFCell cell = row.getCell(cellNum);
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setFillForegroundColor(HSSFColor.RED.index);
				cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(cellStyle);
				
				System.out.println("mark red for row=" + rowIndex);
			}
		}

		workbook.write(new FileOutputStream(outFile));
	}

	private static int min(int a, int b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}
	}

	@SuppressWarnings("unused")
	private static void print(Map<BigDecimal, SortedSet<Integer>> m) {
		for (Map.Entry<BigDecimal, SortedSet<Integer>> entry : m.entrySet()) {
			BigDecimal amt = entry.getKey();
			System.out.print("amt=" + amt + ", row=");
			for (Integer row : entry.getValue()) {
				System.out.print(row + ",");
			}

			System.out.println("");
		}
	}

	public static void main(String[] args) throws Exception {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/收入.xls");
		Map<BigDecimal, SortedSet<Integer>> m1 = loadExcel(in, 0);

		in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/预收0414.xls");
		Map<BigDecimal, SortedSet<Integer>> m2 = loadExcel(in, 1);

		Set<BigDecimal> amt1 = new HashSet<BigDecimal>();
		Set<BigDecimal> amt2 = new HashSet<BigDecimal>();

		for (Map.Entry<BigDecimal, SortedSet<Integer>> entry : m1.entrySet()) {
			BigDecimal amt = entry.getKey();
			SortedSet<Integer> set1 = m1.get(amt);
			SortedSet<Integer> set2 = m2.get(amt);
			if (set2 != null) {
				int s = min(set1.size(), set2.size());
				for (int i = 0; i < s; i++) {
					set1.remove(set1.first());
					set2.remove(set2.first());
				}

				if (set1.isEmpty()) {
					amt1.add(amt);
				}

				if (set2.isEmpty()) {
					amt2.add(amt);
				}
			}
		}

		for (BigDecimal amt : amt1) {
			m1.remove(amt);
		}

		for (BigDecimal amt : amt2) {
			m2.remove(amt);
		}

		//print(m1);

		in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/收入.xls");
		markExcel(in, "/tmp/收入.xls", 0, m1);
		
		System.out.println("-------");

		//print(m2);
		
		in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-data/预收0414.xls");
		markExcel(in, "/tmp/预收0414.xls", 1, m2);
	}

}