package database.validation;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

/**
 * 
 * @author anshuman.sarkar
 * @since 1.0
 * @see GenerateExcel
 * @see AllTables
 */
public class DataEvaluate {

	protected static LinkedList<String> listHeader = new LinkedList<String>();
	protected static LinkedHashMap<String, LinkedList<String>> dataArrayUser1 = new LinkedHashMap<String, LinkedList<String>>();
	protected static LinkedHashMap<String, LinkedList<String>> dataArrayUser2 = new LinkedHashMap<String, LinkedList<String>>();
	protected static LinkedHashMap<String, LinkedList<String>> dataArrayUserCompare = new LinkedHashMap<String, LinkedList<String>>();

	/**
	 * 
	 * @param statement1 Contains the connection details in Statement format for
	 *                   User1 (UAT)
	 * @param statement2 Contains the connection details in Statement format for
	 *                   User2 (Prod)
	 * @param tableName  Contains the Table Name for the data compare
	 * @param fieldName  Contains the fields of the table
	 * @return Type:String Value:True if all are same else False
	 */
	public static String compareHSSF(Statement statement1, Statement statement2, String tableName, String fieldName) {
		listHeader.clear();
		dataArrayUser1.clear();
		dataArrayUser2.clear();
		dataArrayUserCompare.clear();
		String query = "SELECT " + fieldName + " FROM " + tableName;
		getDataUser1(statement1, query);
		getDataUser2(statement2, query);
		int l = listHeader.size();
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(listHeader);
		for (int i = 0; i < l; i++) {
			listHeader.add(i * 3, list.get(i) + StringConstants.whitespace + StringConstants.UAT);
			listHeader.add(i * 3 + 1, list.get(i) + StringConstants.whitespace + StringConstants.Prod);
		}
		GenerateExcel.generateExcelHeaderData(listHeader);
		dataArrayUser1.forEach((k, v) -> {
			LinkedList<String> compare = new LinkedList<String>();
			if (dataArrayUser2.containsKey(k)) {
				LinkedList<String> value = dataArrayUser2.get(k);
				for (int i = 0; i < value.size(); i++) {
					compare.add(v.get(i));
					compare.add(value.get(i));
					compare.add(value.get(i).equalsIgnoreCase(v.get(i)) ? StringConstants.True
							: StringConstants.False + StringConstants.whitespace + StringConstants.Prod);
				}
			} else {
				for (int j = 0; j < v.size(); j++) {
					compare.add(v.get(j));
					compare.add(StringConstants.Hyphen);
					compare.add(StringConstants.notInProd);
				}
			}
			dataArrayUserCompare.put(k, compare);
		});
		dataArrayUser2.forEach((k, v) -> {
			LinkedList<String> compare = new LinkedList<String>();
			if (dataArrayUserCompare.containsKey(k)) {
			} else {
				if (dataArrayUser1.containsKey(k)) {
					LinkedList<String> value = dataArrayUser1.get(k);
					for (int i = 0; i < value.size(); i++) {
						compare.add(v.get(i));
						compare.add(value.get(i));
						compare.add(value.get(i).equalsIgnoreCase(v.get(i)) ? StringConstants.True
								: StringConstants.False + StringConstants.whitespace + StringConstants.UAT);
					}
				} else if (!(dataArrayUserCompare.containsKey(k)))
					for (int j = 0; j < v.size(); j++) {
						compare.add(StringConstants.Hyphen);
						compare.add(v.get(j));
						compare.add(StringConstants.notInUAT);
					}
				dataArrayUserCompare.put(k, compare);
			}
		});
		TreeSet<String> tempSet = new TreeSet<String>();
		LinkedHashMap<String, LinkedList<String>> finaleCheck = new LinkedHashMap<String, LinkedList<String>>();
		for (Map.Entry<String, LinkedList<String>> entry : dataArrayUserCompare.entrySet()) {
			String k = entry.getKey();
			k = (k == null) ? "null" : k;
			LinkedList<String> v = entry.getValue();
			if (v.contains(StringConstants.False + StringConstants.whitespace + StringConstants.Prod)
					|| v.contains(StringConstants.False + StringConstants.whitespace + StringConstants.UAT)) {
				finaleCheck.put(k, v);
				tempSet.add(k);
			}
		}
		for (Map.Entry<String, LinkedList<String>> entry : dataArrayUserCompare.entrySet()) {
			String k = entry.getKey();
			k = (k == null) ? "null" : k;
			LinkedList<String> v = entry.getValue();
			if (v.contains(StringConstants.notInUAT) || v.contains(StringConstants.notInProd)) {
				finaleCheck.put(k, v);
				tempSet.add(k);
			}
		}
		for (Iterator<String> iterator = tempSet.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			dataArrayUserCompare.remove(string);
		}
		finaleCheck.putAll(dataArrayUserCompare);
		dataArrayUserCompare.clear();
		dataArrayUserCompare.putAll(finaleCheck);
		GenerateExcel.generateExcel(dataArrayUserCompare);
		Collection<LinkedList<String>> valueList = dataArrayUserCompare.values();
		String isSame = StringConstants.True;
		for (Iterator<LinkedList<String>> iterator = valueList.iterator(); iterator.hasNext();) {
			LinkedList<String> linkedList = (LinkedList<String>) iterator.next();
			boolean isDiff = false;
			for (int i = 1; i < linkedList.size(); i++) {
				if (i % 3 == 2)
					if (linkedList.get(i).contains(StringConstants.False))
						isDiff = true;
			}
			if (isDiff || linkedList.contains(StringConstants.notInUAT)
					|| linkedList.contains(StringConstants.notInProd)) {
				isSame = StringConstants.False;
				break;
			}
		}
		return isSame;
	}

	/**
	 * 
	 * @param statement1 Contains the connection details in Statement format for
	 *                   User1 (UAT)
	 * @param query      Contains the query which will be used for execution
	 */
	private static void getDataUser1(Statement statement1, String query) {
		ResultSet resultSet = null;
		try {
			resultSet = statement1.executeQuery(query);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				listHeader.add(resultSetMetaData.getColumnName(i));
			}
			while (resultSet.next()) {
				LinkedList<String> list = new LinkedList<String>();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					list.add(resultSet.getString(i) == null ? StringConstants.Null : resultSet.getString(i));
				}
				dataArrayUser1.put(resultSet.getString(1), list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
	}

	/**
	 * 
	 * @param statement2 Contains the connection details in Statement format for
	 *                   User2 (Prod)
	 * @param query      Contains the query which will be used for execution
	 */
	private static void getDataUser2(Statement statement2, String query) {
		ResultSet resultSet = null;
		try {
			resultSet = statement2.executeQuery(query);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			while (resultSet.next()) {
				LinkedList<String> list = new LinkedList<String>();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					list.add(resultSet.getString(i) == null ? StringConstants.Null : resultSet.getString(i));
				}
				dataArrayUser2.put(resultSet.getString(1), list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
	}
}