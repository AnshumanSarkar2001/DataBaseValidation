package database.validation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.aspose.cells.Workbook;

/**
 * 
 * @author anshuman.sarkar
 * @since 1.0
 * @see TableCompare
 * @see DataEvaluate
 * @see AllTables
 */
public class GenerateExcel {

	protected static int count = 0;
	protected static int sheetCount = 0;
	protected static int numberOfColumns = 0;

	protected static String tableName = null;
	protected static String sheetName = null;
	public static LinkedList<String> fileNameList = new LinkedList<String>();

	public static HSSFWorkbook workbook = null;
	public static HSSFSheet sheet = null;
	public static HSSFRow rowhead = null;

	/**
	 * To create a new workbook to work on
	 */
	public static void createWorkbook() {
		workbook = new HSSFWorkbook();
	}

	/**
	 * To create sheet to work on
	 * 
	 * @param TableName Contains the name to give to sheet
	 * @param length    contains half the number of columns
	 */
	public static void createSheet(String TableName, int length) {
		tableName = TableName;
		sheetName = tableName;
		sheet = null;
		sheet = workbook.createSheet(tableName);
		numberOfColumns = length * 2;
	}

	/**
	 * To create sheet to work on
	 * 
	 * @param key       Contains the sheet limit name to give to class
	 * @param length    Contains the actual number of columns
	 * @param TableName Contains the actual name of the table
	 */
	public static void createSheet(String key, int length, String TableName) {
		tableName = TableName;
		sheet = null;
		sheetName = key;
		sheet = workbook.createSheet(key);
		numberOfColumns = length;
	}

	/**
	 * getting data to add in cell
	 * 
	 * @param value Data to add to cell to generate the report file
	 */
	public static void generateExcelDescribe(LinkedHashMap<String, LinkedList<String>> value) {
		sheet = workbook.getSheet(sheetName);
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		HSSFCellStyle stylerNormal = generateStylerNormal();
		HSSFCellStyle diffCellStyle = generateDiffCellStyle();
		value.forEach((k, v) -> {
			HSSFRow rowhead = sheet.createRow(count++);
			rowhead.createCell(0).setCellValue(k);
			boolean isDiff = false;
			for (int i = 0; i < v.size(); i++) {
				rowhead.createCell(i + 1).setCellValue(v.get(i));
				if (v.get(i).contains(StringConstants.No))
					isDiff = true;
			}
			if (v.contains(StringConstants.No) || isDiff)
				generateStyleHM(stylerDifferent, rowhead, diffCellStyle);
			else
				generateStyleHM(stylerNormal, rowhead, null);
		});
	}

	/**
	 * This function only adds style to the cell
	 * 
	 * @param hssfCellStyle Containing the cell style for the cell which don't have
	 *                      the data that contains No
	 * @param hssfRow       Row of the excel recently worked on
	 * @param diffCellStyle Containing the cell style for the cell which contains No
	 *                      else will contain null
	 */
	private static void generateStyleHM(HSSFCellStyle hssfCellStyle, HSSFRow hssfRow, HSSFCellStyle diffCellStyle) {
		hssfRow.forEach((cell) -> {
			cell.setCellStyle(hssfCellStyle);
			if (cell.getStringCellValue().contains(StringConstants.No))
				cell.setCellStyle(diffCellStyle);
		});
	}

	/**
	 * This function generates the header of the Type check main sheet
	 * 
	 * @param list     Contains the header level data
	 * @param numTable Contains total number of tables
	 * @param numDiff  Contains total number of different tables
	 * @param numNot   Contains total number of tables not present in either UAT or
	 *                 Prod
	 */
	public static void generateExcelHeaderLHM(LinkedList<String> list, int numTable, int numDiff, int numNot) {
		count = 0;
		sheet = workbook.getSheet(sheetName);
		sheet.createFreezePane(0, 5);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 3));
		HSSFRow rowhead = sheet.createRow(count++);
		HSSFCellStyle style1 = workbook.createCellStyle();
		HSSFFont font1 = workbook.createFont();
		font1.setBold(true);
		font1.setFontHeightInPoints((short) 24);
		style1.setFont(font1);
		style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
		rowhead.createCell(0).setCellValue(StringConstants.summarySheet + StringConstants.colon
				+ StringConstants.whitespace + DBValidateV1.dbName);
		rowhead.getCell(0).setCellStyle(style1);
		HSSFCellStyle style2 = workbook.createCellStyle();
		HSSFFont font2 = workbook.createFont();
		font2.setBold(true);
		style2.setFont(font2);
		rowhead = sheet.createRow(count++);
		rowhead.createCell(0).setCellValue(StringConstants.totalTables + numTable);
		rowhead.getCell(0).setCellStyle(style2);
		rowhead = sheet.createRow(count++);
		rowhead.createCell(0).setCellValue(StringConstants.totalDiffTables + numDiff);
		rowhead.getCell(0).setCellStyle(style2);
		rowhead = sheet.createRow(count++);
		rowhead.createCell(0).setCellValue(StringConstants.totalTablesNotPresent + numNot);
		rowhead.getCell(0).setCellStyle(style2);
		rowhead = sheet.createRow(count++);
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
		for (int i = 0; i < list.size(); i++) {
			rowhead.createCell(i).setCellValue(list.get(i));
			rowhead.getCell(i).setCellStyle(style);
		}
	}

	/**
	 * 
	 * @param finalCheck contains the datas of the table to add in the cell
	 */
	public static void generateExcelLHM(LinkedHashMap<String, LinkedList<String>> finalCheck) {
		sheet = workbook.getSheet(sheetName);
		int counter = 0;
		HSSFCellStyle stylerNotIN = generateStylerNotIN();
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		HSSFCellStyle stylerNormal = generateStylerNormal();
		HSSFCellStyle diffCellStyle = generateDiffCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		for (Map.Entry<String, LinkedList<String>> entry : finalCheck.entrySet()) {
			String k = entry.getKey();
			LinkedList<String> v = entry.getValue();
			HSSFRow rowhead = sheet.createRow(count++);
			rowhead.createCell(0).setCellValue(k);
			boolean isDiff = false;
			Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
			for (int i = 0; i < v.size(); i++) {
				if (v.get(i).contains(StringConstants.False)) {
					isDiff = true;
					String s = v.get(i);
					v.remove(i);
					v.add(i, s + " Sheet number " + counter);
					link.setAddress(StringConstants.singleQuote + Integer.toString(counter)
							+ StringConstants.singleQuote + StringConstants.Exclaim + StringConstants.first);
					counter += 1;
				}
				rowhead.createCell(i + 1).setCellValue(v.get(i));
			}
			if (v.contains(StringConstants.naProd) || v.contains(StringConstants.naUat))
				generateStyleTM(stylerNotIN, rowhead, null, null, null);
			else if (v.contains(StringConstants.False) || isDiff)
				generateStyleTM(stylerNormal, rowhead, stylerDifferent, diffCellStyle, link);
			else
				generateStyleTM(stylerNormal, rowhead, null, null, null);
		}

	}

	/**
	 * 
	 * @param hssfCellStyle  Contains the style to add to cell of default data
	 * @param hssfRow        Contains the recent row worked on
	 * @param diffCellStyle  Contains the style to add to cell for the data that are
	 *                       different
	 * @param diffCellStyle2 Contains the style to add to cell for the data that are
	 *                       not present
	 * @param link           Contains the link to hyperlink in the cell
	 */
	private static void generateStyleTM(HSSFCellStyle hssfCellStyle, HSSFRow hssfRow, HSSFCellStyle diffCellStyle,
			HSSFCellStyle diffCellStyle2, Hyperlink link) {
		hssfRow.forEach((cell) -> {
			cell.setCellStyle(hssfCellStyle);
			if (cell.getStringCellValue().contains(StringConstants.falseDataMismatch)) {
				cell.setCellStyle(diffCellStyle);
				cell.setHyperlink(link);
			} else if (cell.getStringCellValue().contains(StringConstants.falseFieldNotPresent)) {
				cell.setCellStyle(diffCellStyle2);
				cell.setHyperlink(link);
			}
		});

	}

	/**
	 * 
	 * @param finale Contains the data to be filled in the sheet
	 */
	public static void generateExcel(LinkedList<LinkedList<String>> finale) {
		sheet = workbook.getSheet(sheetName);
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		for (Iterator<LinkedList<String>> iterator = finale.iterator(); iterator.hasNext();) {
			HSSFRow rowhead = sheet.createRow(count++);
			LinkedList<String> linkedList = (LinkedList<String>) iterator.next();
			for (int i = 0; i < linkedList.size(); i++) {
				rowhead.createCell(i).setCellValue(linkedList.get(i));
			}
			if (linkedList.contains(StringConstants.Different) || linkedList.contains(StringConstants.colNotInProd)
					|| linkedList.contains(StringConstants.NA))
				generateStyle(stylerDifferent, rowhead, null);
		}
	}

	/**
	 * 
	 * @param hashmapArray Contains the data to be filled in the sheet
	 */
	public static void generateExcel(LinkedHashMap<String, LinkedList<String>> hashmapArray) {
		sheet = workbook.getSheet(sheetName);
		HSSFCellStyle stylerNotIN = generateStylerNotIN();
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		HSSFCellStyle stylerNormal = generateStylerNormal();
		HSSFCellStyle diffCellStyle = generateDiffCellStyle();
		hashmapArray.forEach((k, v) -> {
			HSSFRow rowhead = sheet.createRow(count++);
			rowhead.createCell(0).setCellValue(k);
			boolean isDiff = false;
			for (int i = 1; i < v.size(); i++) {
				String val=v.get(i).length() > 450 ? v.get(i).substring(0, 400) + StringConstants.TripleDot : v.get(i);
				rowhead.createCell(i).setCellValue(String.valueOf(val));
				if (i % 3 == 2)
					if (v.get(i).contains(StringConstants.False))
						isDiff = true;
			}
			if (v.contains(StringConstants.notInProd) || v.contains(StringConstants.notInUAT))
				generateStyle(stylerNotIN, rowhead, null);
			else if (v.contains(StringConstants.False) || isDiff)
				generateStyle(stylerDifferent, rowhead, diffCellStyle);
			else
				generateStyle(stylerNormal, rowhead, null);
		});
	}

	/**
	 * 
	 * @param hssfCellStyle Contains the cell style for the cell that satisfies a
	 *                      condition
	 * @param hssfRow       Contains the recent row worked on
	 * @param diffCellStyle Contains the cell style for the cell that contains False
	 *                      values
	 */
	private static void generateStyle(HSSFCellStyle hssfCellStyle, HSSFRow hssfRow, HSSFCellStyle diffCellStyle) {
		hssfRow.forEach((cell) -> {
			cell.setCellStyle(hssfCellStyle);
			if (cell.getStringCellValue().contains(StringConstants.False)) {
				cell.setCellStyle(diffCellStyle);
			}
		});
	}

	/**
	 * 
	 * @return Style that will be used to show what cells are different
	 */
	private static HSSFCellStyle generateDiffCellStyle() {
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setWrapText(true);
		return style;
	}

	/**
	 * 
	 * @return Style that will be used to show which cells contains the value
	 *         containing Not in.
	 */
	public static HSSFCellStyle generateStylerNotIN() {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setWrapText(true);
		return style;
	}

	/**
	 * 
	 * @return Style that will be used to show which row contains the cells that are
	 *         different
	 */
	public static HSSFCellStyle generateStylerDifferent() {
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setWrapText(true);
		return style;
	}

	/**
	 * 
	 * @return Style for general cells
	 */
	public static HSSFCellStyle generateStylerNormal() {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setWrapText(true);
		return style;
	}

	public static void generateExcelHeaderData(LinkedList<String> listHeader) {
		count = 0;
		sheet = workbook.getSheet(sheetName);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		sheet.createFreezePane(0, 2);
		HSSFRow rowhead = sheet.createRow(count++);
		HSSFCellStyle style1 = workbook.createCellStyle();
		HSSFFont font1 = workbook.createFont();
		HSSFCellStyle style2 = workbook.createCellStyle();
		HSSFFont font2 = workbook.createFont();
		HSSFCreationHelper creator = workbook.getCreationHelper();
		HSSFHyperlink link = creator.createHyperlink(HyperlinkType.DOCUMENT);
		link.setAddress(StringConstants.singleQuote + StringConstants.Data_Summary + StringConstants.singleQuote
				+ StringConstants.Exclaim + StringConstants.first);
		font1.setBold(true);
		font1.setFontHeightInPoints((short) 20);
		style1.setFont(font1);
		style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
		rowhead.createCell(0).setCellValue(tableName);
		rowhead.createCell(5).setCellValue(StringConstants.mainSheet);
		rowhead.getCell(0).setCellStyle(style1);
		style2.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		font2.setBold(true);
		font2.setFontHeightInPoints((short) 10);
		style2.setFont(font2);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setWrapText(true);
		rowhead.getCell(5).setCellStyle(style2);
		rowhead.getCell(5).setHyperlink(link);
		rowhead = sheet.createRow(count++);
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
		for (int i = 0; i < listHeader.size(); i++) {
			rowhead.createCell(i).setCellValue(listHeader.get(i));
			rowhead.getCell(i).setCellStyle(style);
		}
	}

	/**
	 * 
	 * @param listHeader Contains the header level data to be filled in the sheet
	 */
	public static void generateExcelHeader(LinkedList<String> listHeader) {
		count = 0;
		sheet = workbook.getSheet(sheetName);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		sheet.createFreezePane(0, 2);
		HSSFRow rowhead = sheet.createRow(count++);
		HSSFCellStyle style1 = workbook.createCellStyle();
		HSSFFont font1 = workbook.createFont();
		HSSFCellStyle style2 = workbook.createCellStyle();
		HSSFFont font2 = workbook.createFont();
		HSSFCreationHelper creator = workbook.getCreationHelper();
		HSSFHyperlink link = creator.createHyperlink(HyperlinkType.DOCUMENT);
		link.setAddress(StringConstants.singleQuote + StringConstants.Checker + StringConstants.singleQuote
				+ StringConstants.Exclaim + StringConstants.first);
		font1.setBold(true);
		font1.setFontHeightInPoints((short) 20);
		style1.setFont(font1);
		style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
		rowhead.createCell(0).setCellValue(tableName);
		rowhead.createCell(5).setCellValue(StringConstants.mainSheet);
		rowhead.getCell(0).setCellStyle(style1);
		style2.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		font2.setBold(true);
		font2.setFontHeightInPoints((short) 10);
		style2.setFont(font2);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setWrapText(true);
		rowhead.getCell(5).setCellStyle(style2);
		rowhead.getCell(5).setHyperlink(link);
		rowhead = sheet.createRow(count++);
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
		for (int i = 0; i < listHeader.size(); i++) {
			rowhead.createCell(i).setCellValue(listHeader.get(i));
			rowhead.getCell(i).setCellStyle(style);
		}
	}

	public static void autoSizeColumn() {
		for (int i = 0; i < sheet.getLastRowNum();) {
			sheet.autoSizeColumn(i++);
		}
	}

	public static void specialColumnSize() {
		sheet.setDefaultColumnWidth(22000);
		for (int i = 0; i < numberOfColumns; i++)
			if (i % 3 == 2)
				sheet.autoSizeColumn(i);
			else
				sheet.setColumnWidth(i, 30 * 256);
	}

	public static void fixedColumnSize() {
		sheet.setDefaultColumnWidth(22000);
		for (int i = 0; i < numberOfColumns; i++)
			sheet.setColumnWidth(i, 40 * 256);
	}

	/**
	 * 
	 * @param directory Contains the path to save the excel file to.
	 * @param fileName  Contains the file name that will be used to save the Excel
	 *                  file as.
	 */
	public static void dumpExcelFile(String directory, String fileName) {
		try {
			workbook.write(new FileOutputStream(
					directory + StringConstants.backslash + fileName + StringConstants.EXTENSION_XLS));
			fileNameList.add(directory + StringConstants.backslash + fileName + StringConstants.EXTENSION_XLS);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
	}

	public static void deleteFiles() {
		fileNameList.forEach((v) -> {
			File file = new File(v);
			file.delete();
		});
	}

	/**
	 * 
	 * @param directory Contains the path to save the Excel file to.
	 * @param lastName  Contains the last name that will be attached to the name
	 *                  generated to save the Excel file as.
	 * @return File name of the final generated Excel file.
	 */
	public static String merger(String directory, String lastName) {
		Workbook SourceBook1 = null;
		String fileName = null;
		try {
			SourceBook1 = new Workbook(fileNameList.get(0));
			for (int i = 1; i < fileNameList.size(); i++) {
				Workbook SourceBook2;
				try {
					SourceBook2 = new Workbook(fileNameList.get(i));
					SourceBook1.combine(SourceBook2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fileName = GenerateFileName.generateFileName();
			SourceBook1.save(directory + StringConstants.backslash + fileName + StringConstants.underscore + lastName
					+ StringConstants.EXTENSION_XLSX);
			System.out.println("Excel File generated Successfully!");
			deleteFiles();
			fileNameList.clear();
		} catch (Exception e1) {
			e1.printStackTrace();
			DBValidateV1.closeConnection();
		}
		return fileName = fileName + StringConstants.underscore + lastName + StringConstants.EXTENSION_XLS;
	}

	/**
	 * 
	 * @param dataArrayUserCompare Contains the data to be filled in the sheet
	 */
	public static void generateExcelCompare(LinkedHashMap<String, LinkedList<String>> dataArrayUserCompare) {
		sheet = workbook.getSheet(sheetName);
		HSSFCellStyle stylerNotIN = generateStylerNotIN();
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		HSSFCellStyle stylerNormal = generateStylerNormal();
		HSSFCellStyle diffCellStyle = generateDiffCellStyle();
		dataArrayUserCompare.forEach((k, v) -> {
			HSSFRow rowhead = sheet.createRow(count++);
			rowhead.createCell(0).setCellValue(k);
			boolean isDiff = false;
			for (int i = 1; i < v.size(); i++) {
				rowhead.createCell(i).setCellValue(v.get(i));
				if (i % 3 == 2)
					if (v.get(i).contains(StringConstants.False))
						isDiff = true;
			}
			if (v.contains(StringConstants.notInProd))
				generateStyle(stylerNotIN, rowhead, null);
			else if (v.contains(StringConstants.False) || isDiff)
				generateStyle(stylerDifferent, rowhead, diffCellStyle);
			else
				generateStyle(stylerNormal, rowhead, null);
		});
	}

	/**
	 * 
	 * @param treeMap Contains the data to be filled in the sheet
	 */
	public static void generateExcelSpecial(LinkedHashMap<String, String> treeMap) {
		sheet = workbook.getSheet(sheetName);
		HSSFCellStyle stylerDifferent = generateStylerDifferent();
		HSSFCellStyle stylerNormal = generateStylerNormal();
		HSSFCellStyle diffCellStyle = generateDiffCellStyle();
		treeMap.forEach((k, v) -> {
			HSSFRow rowhead = sheet.createRow(count++);
			rowhead.createCell(0).setCellValue(k);
			rowhead.createCell(1).setCellValue(v);
			if (v.contains(StringConstants.False))
				generateStyleSpecial(stylerDifferent, rowhead, diffCellStyle);
			else
				generateStyleSpecial(stylerNormal, rowhead, null);
		});
	}

	private static void generateStyleSpecial(HSSFCellStyle hssfCellStyle, HSSFRow hssfRow,
			HSSFCellStyle diffCellStyle) {
		hssfRow.forEach((cell) -> {
			cell.setCellStyle(hssfCellStyle);
			if (cell.getStringCellValue().contains(StringConstants.False)) {
				String s = cell.getStringCellValue();
				s = s.replaceAll("^[^:]+:\\s|\\)$", "");
				HSSFCreationHelper creator = workbook.getCreationHelper();
				HSSFHyperlink link = creator.createHyperlink(HyperlinkType.DOCUMENT);
				link.setAddress(StringConstants.singleQuote + s + StringConstants.singleQuote + StringConstants.Exclaim
						+ StringConstants.first);
				cell.setCellStyle(diffCellStyle);
				cell.setHyperlink(link);
			}
		});
	}

	/**
	 * 
	 * @param list  Contains the header level data to be filled in the sheet
	 * @param total Contains the total number of config tables
	 * @param diff  Contains the total number of different config tables
	 */
	public static void generateExcelSpecialHeader(LinkedList<String> list, int total, int diff) {
		count = 0;
		sheet = workbook.getSheet(sheetName);
		sheet.createFreezePane(0, 5);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
		HSSFRow rowhead = sheet.createRow(count++);
		HSSFCellStyle style1 = workbook.createCellStyle();
		HSSFFont font1 = workbook.createFont();
		font1.setBold(true);
		font1.setFontHeightInPoints((short) 24);
		style1.setFont(font1);
		style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
		rowhead.createCell(0).setCellValue(StringConstants.summarySheet + StringConstants.colon
				+ StringConstants.whitespace + DBValidateV1.dbName);
		rowhead.getCell(0).setCellStyle(style1);
		HSSFCellStyle style2 = workbook.createCellStyle();
		HSSFFont font2 = workbook.createFont();
		font2.setBold(true);
		style2.setFont(font2);
		rowhead = sheet.createRow(count++);
		rowhead.createCell(0).setCellValue(StringConstants.totalConfigTable + total);
		rowhead.getCell(0).setCellStyle(style2);
		rowhead = sheet.createRow(count++);
		rowhead.createCell(0).setCellValue(StringConstants.totalDiffConfigTable + diff);
		rowhead.getCell(0).setCellStyle(style2);
		rowhead = sheet.createRow(count++);
		rowhead = sheet.createRow(count++);
		HSSFCellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		style.setWrapText(true);
		style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
		for (int i = 0; i < list.size(); i++) {
			rowhead.createCell(i).setCellValue(list.get(i));
			rowhead.getCell(i).setCellStyle(style);
		}
	}

	/**
	 * 
	 * @param directory Contains the path name to which the Excel file will be saved
	 *                  to.
	 * @param fileName  Contains the file name which will be used to generate the
	 *                  Excel file as.
	 */
	public static void dumpExcelFileSpecial(String directory, String fileName) {
		try {
			workbook.write(new FileOutputStream(
					directory + StringConstants.backslash + fileName + StringConstants.EXTENSION_XLS));
			fileNameList.add(0, directory + StringConstants.backslash + fileName + StringConstants.EXTENSION_XLS);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
	}
}
