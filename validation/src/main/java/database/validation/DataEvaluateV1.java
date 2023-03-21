package database.validation;

import java.util.*;
import java.sql.*;

public class DataEvaluateV1 {

	protected static LinkedList<String> header_data = new LinkedList<>();
	protected static LinkedList<String> header_data_modified = new LinkedList<>();

	protected static Map<String, Map<String, Map<String, LinkedList<String>>>> dataCheck3orMore = new HashMap<>();
	protected static Map<String, Map<String, LinkedList<String>>> dataCheck3 = new HashMap<>();
	protected static Map<String, LinkedList<String>> dataCheck1or2 = new HashMap<>();
	protected static String query = null;

	protected static void doCheck3orMore(Statement statement1, Statement statement2, String tableName, String fieldName,
			int noFields) {

		query = "SELECT " + fieldName + " FROM " + tableName;
		final String dummyQuery = "SELECT " + fieldName + " FROM " + tableName + " LIMIT 0,0";
		ResultSet resultSet1 = null;
		ResultSet resultSet2 = null;
		try {
			Map<String, Map<String, LinkedList<String>>> set1 = new HashMap<>();
			Map<String, LinkedList<String>> set2 = new HashMap<>();
			{
				resultSet1 = statement1.executeQuery(dummyQuery);
				ResultSetMetaData resultSetMetaData = resultSet1.getMetaData();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					header_data.add(resultSetMetaData.getColumnName(i));
					header_data_modified.add(resultSetMetaData.getColumnName(i)+" UAT");
					header_data_modified.add(resultSetMetaData.getColumnName(i)+" Prod");
					header_data_modified.add("isSame?");
				}
				resultSet1.close();
			}
			resultSet1 = statement1.executeQuery(query);
			resultSet2 = statement2.executeQuery(query);
			{
				while (resultSet1.next()) {
					set1.clear();
					set2.clear();
					LinkedList<String> list = new LinkedList<>();
					for (int i = 4; i < noFields; i++) {
						String listData1 = resultSet1.getString(i);
						listData1 = (listData1 == null) ? "NA in UAT" : listData1;
						String listData2 = "null";
						list.add(listData1);
						list.add(listData2);
					}
					set2.put(resultSet1.getString(3), list);
					set1.put(resultSet1.getString(2), set2);
					dataCheck3orMore.put(resultSet1.getString(1), set1);
				}
			}
			{
				while (resultSet2.next()) {
					set1.clear();
					set2.clear();
					String col1Data = resultSet2.getString(1);
					String col2Data = resultSet2.getString(2);
					String col3Data = resultSet2.getString(3);
					LinkedList<String> list = new LinkedList<>();
					if (dataCheck3orMore.containsKey(col1Data)) {
						set1.putAll(dataCheck3orMore.get(col1Data));
						if (set1.containsKey(col2Data)) {
							set2.putAll(set1.get(col2Data));
							if (set2.containsKey(col3Data)) {
								list.addAll(set2.get(col3Data));
								for (int i = 4; i < noFields; i++) {
									String listData2 = resultSet2.getString(i);
									listData2 = (listData2 == null) ? "NA in Prod" : listData2;
									list.set((i - 4) * 2, listData2);
								}
								set2.put(resultSet1.getString(3), list);
								set1.put(resultSet1.getString(2), set2);
								dataCheck3orMore.put(resultSet1.getString(1), set1);
							} else {
								for (int i = 4; i < noFields; i++) {
									String listData1 = "null";
									String listData2 = resultSet2.getString(i);
									listData2 = (listData2 == null) ? "NA in Prod" : listData2;
									list.add(listData1);
									list.add(listData2);
								}
								set2.put(resultSet1.getString(3), list);
								set1.put(resultSet1.getString(2), set2);
								dataCheck3orMore.put(resultSet1.getString(1), set1);
							}
						} else {
							for (int i = 4; i < noFields; i++) {
								String listData1 = "null";
								String listData2 = resultSet2.getString(i);
								listData2 = (listData2 == null) ? "NA in Prod" : listData2;
								list.add(listData1);
								list.add(listData2);
							}
							set2.put(resultSet1.getString(3), list);
							set1.put(resultSet1.getString(2), set2);
							dataCheck3orMore.put(resultSet1.getString(1), set1);
						}
					} else {
						for (int i = 4; i < noFields; i++) {
							String listData1 = "null";
							String listData2 = resultSet2.getString(i);
							listData2 = (listData2 == null) ? "NA in Prod" : listData2;
							list.add(listData1);
							list.add(listData2);
						}
						set2.put(resultSet1.getString(3), list);
						set1.put(resultSet1.getString(2), set2);
						dataCheck3orMore.put(resultSet1.getString(1), set1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static void doCheck3(Statement statement1, Statement statement2, String tableName, String fieldName) {

		query = "SELECT " + fieldName + " FROM " + tableName;
		final String dummyQuery = "SELECT " + fieldName + " FROM " + tableName + " LIMIT 0,0";
		ResultSet resultSet1 = null;
		ResultSet resultSet2 = null;
		try {
			Map<String, LinkedList<String>> set1 = new HashMap<>();
			{
				resultSet1 = statement1.executeQuery(dummyQuery);
				ResultSetMetaData resultSetMetaData = resultSet1.getMetaData();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					header_data.add(resultSetMetaData.getColumnName(i));
				}
				resultSet1.close();
			}
			resultSet1 = statement1.executeQuery(query);
			resultSet2 = statement2.executeQuery(query);
			{
				while (resultSet1.next()) {
					set1.clear();
					LinkedList<String> list = new LinkedList<>();
					String listData1 = resultSet1.getString(3);
					listData1 = (listData1 == null) ? "NA in UAT" : listData1;
					String listData2 = "null";
					list.add(listData1);
					list.add(listData2);
					set1.put(resultSet1.getString(2), list);
					dataCheck3.put(resultSet1.getString(1), set1);
				}
			}
			{
				while (resultSet2.next()) {
					set1.clear();
					String col1Data = resultSet2.getString(1);
					String col2Data = resultSet2.getString(2);
					LinkedList<String> list = new LinkedList<>();
					if (dataCheck3.containsKey(col1Data)) {
						set1.putAll(dataCheck3.get(col1Data));
						if (set1.containsKey(col2Data)) {
							list.addAll(set1.get(col2Data));
							String listData2 = resultSet2.getString(3);
							listData2 = (listData2 == null) ? "NA in Prod" : listData2;
							list.set(2, listData2);
							set1.put(resultSet1.getString(2), list);
							dataCheck3.put(resultSet1.getString(1), set1);
						} else {
							String listData1 = "null";
							String listData2 = resultSet2.getString(3);
							listData2 = (listData2 == null) ? "NA in Prod" : listData2;
							list.add(listData1);
							list.add(listData2);
							set1.put(resultSet1.getString(2), list);
							dataCheck3.put(resultSet1.getString(1), set1);
						}
					} else {
						String listData1 = "null";
						String listData2 = resultSet2.getString(3);
						listData2 = (listData2 == null) ? "NA in Prod" : listData2;
						list.add(listData1);
						list.add(listData2);
						set1.put(resultSet1.getString(2), list);
						dataCheck3.put(resultSet1.getString(1), set1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static void doCheck2(Statement statement1, Statement statement2, String tableName, String fieldName) {

		query = "SELECT " + fieldName + " FROM " + tableName;
		final String dummyQuery = "SELECT " + fieldName + " FROM " + tableName + " LIMIT 0,0";
		ResultSet resultSet1 = null;
		ResultSet resultSet2 = null;
		try {
			Map<String, LinkedList<String>> set1 = new HashMap<>();
			{
				resultSet1 = statement1.executeQuery(dummyQuery);
				ResultSetMetaData resultSetMetaData = resultSet1.getMetaData();
				for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
					header_data.add(resultSetMetaData.getColumnName(i));
				}
				resultSet1.close();
			}
			resultSet1 = statement1.executeQuery(query);
			resultSet2 = statement2.executeQuery(query);
			{
				while (resultSet1.next()) {
					set1.clear();
					LinkedList<String> list = new LinkedList<>();
					String listData1 = resultSet1.getString(2);
					listData1 = (listData1 == null) ? "NA in UAT" : listData1;
					String listData2 = "null";
					list.add(listData1);
					list.add(listData2);
					dataCheck1or2.put(resultSet1.getString(1), list);
				}
			}
			{
				while (resultSet2.next()) {
					set1.clear();
					String col1Data = resultSet2.getString(1);
					LinkedList<String> list = new LinkedList<>();
					if (dataCheck1or2.containsKey(col1Data)) {
						list.addAll(dataCheck1or2.get(col1Data));
						String listData2 = resultSet2.getString(2);
						listData2 = (listData2 == null) ? "NA in Prod" : listData2;
						list.set(2, listData2);
						dataCheck1or2.put(resultSet1.getString(1), list);
					} else {
						String listData1 = "null";
						String listData2 = resultSet2.getString(2);
						listData2 = (listData2 == null) ? "NA in Prod" : listData2;
						list.add(listData1);
						list.add(listData2);
						dataCheck1or2.put(resultSet1.getString(1), list);
					}
				}
			}
		} catch (

		SQLException e) {
			e.printStackTrace();
		}
	}

}
