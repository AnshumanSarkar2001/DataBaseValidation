package database.validation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author anshuman.sarkar
 * @since 1.0
 * @see GenerateExcel
 * @see DataEvaluate
 */
public class AllTables {
	/**
	 * 
	 * @param statement1 contains the connection for the first user
	 * @param statement2 contains the connection for the second user
	 * @param dbName     contains the database name for the operation to work on
	 * @return fileName of the final report
	 */
	public static String dataCompare(Statement statement1, Statement statement2, String dbName) {
		String fileName = null;
		int count = 1;
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		LinkedList<String> list = new LinkedList<String>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(StringConstants.tablePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		LinkedHashMap<String, String> dataMap = GetDetailsFromFile.getTableandFields(fis);
		int falseCount = 0;
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			try {
				int length = Arrays.asList(val.split(StringConstants.comma)).size();
				GenerateExcel.createWorkbook();
				String sheetName = key.length() > 20 ? key.substring(0, 20) + (count += 1) : key;
				GenerateExcel.createSheet(sheetName, length * 3, key);
				GenerateExcel.specialColumnSize();
//			GenerateExcel.fixedColumnSize();
				String string = DataEvaluate.compareHSSF(statement1, statement2, key, val);
				if (string.equals(StringConstants.False)) {
					string += StringConstants.whitespace + StringConstants.openRoundBraces + StringConstants.Sheet
							+ StringConstants.colon + StringConstants.whitespace + sheetName
							+ StringConstants.closeRoundBraces;
					falseCount += 1;
				}
				treeMap.put(key, string);
				fileName = GenerateFileName.generateFileName();
				GenerateExcel.dumpExcelFile(StringConstants.directory, fileName + key);
				System.out.println("Done... " + key);
			} catch (IllegalArgumentException e) {
				System.err.println("Failed: " + key);
				continue;
			}
		}
		list.add(StringConstants.tableName);
		list.add(StringConstants.Bool);
		GenerateExcel.createWorkbook();
		GenerateExcel.createSheet(StringConstants.Data_Summary, 2, null);
		GenerateExcel.fixedColumnSize();
		GenerateExcel.generateExcelSpecialHeader(list, treeMap.size(), falseCount);
		TreeSet<String> tempSet = new TreeSet<String>();
		LinkedHashMap<String, String> finaleCheck = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();
			if (v.contains(StringConstants.False)) {
				finaleCheck.put(k, v);
				tempSet.add(k);
			}
		}
		for (Iterator<String> iterator = tempSet.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			treeMap.remove(string);
		}
		finaleCheck.putAll(treeMap);
		GenerateExcel.generateExcelSpecial(finaleCheck);
		GenerateExcel.dumpExcelFileSpecial(StringConstants.directory, StringConstants.Data_Summary);

		fileName = GenerateExcel.merger(StringConstants.directory, StringConstants.Data);
		return fileName;
	}
}
