package database.validation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Gets the credential from the file given and also gives the tablename with
 * fields which will be used for comparing of data
 * 
 * @author anshuman.sarkar
 * @since 1.0
 */
public class GetDetailsFromFile {

	protected static LinkedHashMap<String, String> datacenter = new LinkedHashMap<String, String>();

	/**
	 * Gets the credentials given in the format
	 * 
	 * <pre>
	 * userName = user1Name , user2Name
	 * password = user1Password , user2Password
	 * proxyPassword = null , user2ProxyPassword
	 * </pre>
	 * 
	 * If any spaces are there in between the values it will trim them for smooth
	 * functioning and error free ride. But if it does give error, please verify if
	 * the credentials are given correctly or not.
	 * <p>
	 * 
	 * 
	 * @param fileInputStream Contains the file that contains the credential that
	 *                        will be used
	 * @return LinkedHashMap That contains the credentials that the file was
	 *         containing
	 */
	protected static HashMap<String, LinkedList<String>> getDetailsFromProperties(FileInputStream fileInputStream) {
		Properties prop = new Properties();
		try {
			prop.load(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		HashMap<String, LinkedList<String>> hmArray = new HashMap<String, LinkedList<String>>();
		for (Entry<Object, Object> entry : prop.entrySet()) {
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			LinkedList<String> list = new LinkedList<String>();
			Collections.addAll(list, StringUtils.stripAll(val.split(StringConstants.comma)));
			hmArray.put(key, list);
		}
		return hmArray;
	}

	/**
	 * This function gets all the Tables with their fields that will be used for
	 * data compare
	 * 
	 * @return HashMap containing the Table name as keys and fields name as values
	 */
	public static LinkedHashMap<String, String> getTableandFields(FileInputStream fileInputStream) {
		Properties prop = new Properties();
		datacenter.clear();
		try {
			prop.load(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		for (Entry<Object, Object> entry : prop.entrySet()) {
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			datacenter.put(key, val);
		}
		return datacenter;
	}
}
