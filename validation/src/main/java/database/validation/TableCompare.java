package database.validation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 
 * @author anshuman.sarkar
 * @since 1.0
 * @see GenerateExcel
 */
public class TableCompare {
	/**
	 * 
	 * @param statement1 Contains the connection details in Statement format for
	 *                   User1 (UAT)
	 * @param statement2 Contains the connection details in Statement format for
	 *                   User2 (Prod)
	 * @param dbName     Contains the DataBase name on which the checking is to be
	 *                   done
	 * @return FileName of the final report file generated
	 */
	public static String getAllTables(Statement statement1, Statement statement2, String dbName) {

		System.out.println("Running SQL Query....\n\n");

		GenerateExcel.createWorkbook();
		GenerateExcel.createSheet(StringConstants.Checker, 2);
		GenerateExcel.fixedColumnSize();

		String show = "SHOW TABLES";
		LinkedList<String> showcase1 = new LinkedList<String>();
		LinkedList<String> showcase2 = new LinkedList<String>();
		Set<String> NotIn1 = new HashSet<String>();
		Map<String, LinkedList<String>> NotIn = new HashMap<String, LinkedList<String>>();
		Map<String, LinkedHashMap<String, LinkedList<String>>> backUp = new HashMap<String, LinkedHashMap<String, LinkedList<String>>>();
		Set<String> In = new HashSet<String>();

		ResultSet resultSet1 = null;
		ResultSet resultSet2 = null;
		try {
			resultSet1 = statement1.executeQuery(show);
			resultSet2 = statement2.executeQuery(show);

			while (resultSet1.next()) {
				if (!resultSet1.getString(1).contains(StringConstants.acct_doc_headerrelated))
					showcase1.add(resultSet1.getString(1));
			}
			resultSet1.close();

			while (resultSet2.next()) {
				if (!resultSet2.getString(1).contains(StringConstants.acct_doc_headerrelated))
					showcase2.add(resultSet2.getString(1));
			}
			resultSet2.close();
		} catch (SQLException e) {
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}

		for (Iterator<String> iterator = showcase1.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (!showcase2.contains(string)) {
				LinkedList<String> linkedlist = new LinkedList<String>();
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.No);
				linkedlist.add(StringConstants.naProd);
				NotIn.put(string, linkedlist);
			} else
				In.add(string);
		}
		for (Iterator<String> iterator = showcase2.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			if (!showcase1.contains(string) && !NotIn.containsKey(string)) {
				LinkedList<String> linkedlist = new LinkedList<String>();
				linkedlist.add(StringConstants.No);
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.naUat);
				NotIn.put(string, linkedlist);
			} else if (!NotIn1.contains(string))
				In.add(string);
		}

		LinkedList<String> temp = new LinkedList<>();
		temp.add(StringConstants.tableName);
		temp.add(dbName + StringConstants.whitespace + StringConstants.UAT);
		temp.add(dbName + StringConstants.whitespace + StringConstants.Prod);
		temp.add(StringConstants.Bool);
//		System.out.println("Done Present or not");
		TreeMap<String, LinkedList<String>> finalCheck = new TreeMap<String, LinkedList<String>>();
		finalCheck.putAll(NotIn);

		LinkedList<String> Inside = new LinkedList<String>(In);
		for (int counter = 0; counter < Inside.size(); counter += 1) {
			LinkedHashMap<String, LinkedList<String>> finale = new LinkedHashMap<String, LinkedList<String>>();
			String query = "SELECT column_name,column_type FROM information_schema.columns WHERE table_name = '"
					+ Inside.get(counter) + "' ORDER BY table_name ASC";
			boolean isDiff = false;
			boolean isNotPresent = false;
			ResultSet rS1 = null;
			ResultSet rS2 = null;
			try {
				rS1 = statement1.executeQuery(query);
				rS2 = statement2.executeQuery(query);

				while (rS1.next()) {
					LinkedList<String> values = new LinkedList<String>();
					values.add(rS1.getString(2));
					values.add(StringConstants.notPresent);
					values.add(StringConstants.No);
					finale.put(rS1.getString(1), values);
				}
				while (rS2.next()) {
					if (finale.containsKey(rS2.getString(1))) {
						LinkedList<String> values = finale.get(rS2.getString(1));
						values.set(1, rS2.getString(2));
						values.set(2, values.get(0).equals(values.get(1)) ? StringConstants.Yes : StringConstants.No);
						finale.put(rS2.getString(1), values);
					} else {
						LinkedList<String> values = new LinkedList<String>();
						values.add(StringConstants.notPresent);
						values.add(rS2.getString(2));
						values.add(StringConstants.No);
						finale.put(rS2.getString(1), values);
					}
				}
				rS1.close();
				rS2.close();
			} catch (SQLException e) {
				e.printStackTrace();
				DBValidateV1.closeConnection();
			}
			LinkedHashMap<String, LinkedList<String>> lhmfinale = new LinkedHashMap<String, LinkedList<String>>();
			Set<String> tempSetHead = new HashSet<String>();
			for (Map.Entry<String, LinkedList<String>> entry : finale.entrySet()) {
				String k = entry.getKey();
				LinkedList<String> v = entry.getValue();
				if (v.contains(StringConstants.No)) {
					lhmfinale.put(k, v);
					tempSetHead.add(k);
				}
			}
			for (Iterator<String> iterator = tempSetHead.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				finale.remove(string);
			}
			lhmfinale.putAll(finale);
			finale = lhmfinale;

			for (Entry<String, LinkedList<String>> entry : finale.entrySet()) {
				LinkedList<String> val = entry.getValue();
				if (val.contains(StringConstants.No))
					isDiff = true;
				if (val.contains(StringConstants.notPresent))
					isNotPresent = true;
			}

			if (isNotPresent) {
				LinkedList<String> linkedlist = new LinkedList<String>();
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.falseFieldNotPresent);
				finalCheck.put(Inside.get(counter), linkedlist);
				backUp.put(Inside.get(counter), finale);
			} else if (isDiff) {
				LinkedList<String> linkedlist = new LinkedList<String>();
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.falseDataMismatch);
				finalCheck.put(Inside.get(counter), linkedlist);
				backUp.put(Inside.get(counter), finale);
			} else {
				LinkedList<String> linkedlist = new LinkedList<String>();
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.Yes);
				linkedlist.add(StringConstants.True);
				finalCheck.put(Inside.get(counter), linkedlist);
			}
			System.out.println("Done... " + Inside.get(counter));
		}
		GenerateExcel.generateExcelHeaderLHM(temp, NotIn.size() + In.size(), backUp.size(), NotIn.size());
		TreeSet<String> tempSet = new TreeSet<String>();
		LinkedHashMap<String, LinkedList<String>> finaleCheck = new LinkedHashMap<String, LinkedList<String>>();
		for (Map.Entry<String, LinkedList<String>> entry : finalCheck.entrySet()) {
			String k = entry.getKey();
			LinkedList<String> v = entry.getValue();
			if (v.contains(StringConstants.falseFieldNotPresent) || v.contains(StringConstants.falseDataMismatch)) {
				finaleCheck.put(k, v);
				tempSet.add(k);
			}
		}
		for (Map.Entry<String, LinkedList<String>> entry : finalCheck.entrySet()) {
			String k = entry.getKey();
			LinkedList<String> v = entry.getValue();
			if (v.contains(StringConstants.naUat) || v.contains(StringConstants.naProd)) {
				finaleCheck.put(k, v);
				tempSet.add(k);
			}
		}
		for (Iterator<String> iterator = tempSet.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			finalCheck.remove(string);
		}
		finaleCheck.putAll(finalCheck);

		GenerateExcel.generateExcelLHM(finaleCheck);
		GenerateExcel.dumpExcelFile(StringConstants.directory,
				GenerateFileName.generateFileName() + StringConstants.underscore + StringConstants.Check);

		List<String> keyList = backUp.keySet().stream().collect(Collectors.toList());
		backUp.forEach((k, v) -> {
			GenerateExcel.createWorkbook();
			GenerateExcel.createSheet(Integer.toString(keyList.indexOf(k)), 4, k);
			GenerateExcel.fixedColumnSize();
			LinkedList<String> temporary = new LinkedList<>();
			temporary.add(StringConstants.field);
			temporary.add(dbName + StringConstants.whitespace + StringConstants.UAT);
			temporary.add(dbName + StringConstants.whitespace + StringConstants.Prod);
			temporary.add(StringConstants.isEqual);
			GenerateExcel.generateExcelHeader(temporary);
			GenerateExcel.generateExcelDescribe(v);
			GenerateExcel.dumpExcelFile(StringConstants.directory,
					GenerateFileName.generateFileName() + StringConstants.underscore + k);
		});

		String fileNameString = GenerateExcel.merger(StringConstants.directory, StringConstants.compare);

		return fileNameString;
	}
}
