package database.validation;

/**
 * This class contains the constants that are used in the whole agent changing
 * to constant files for predefined variables
 * 
 * @author anshuman.sarkar
 * @since 1.6
 */
public final class StringConstants {

	// For DBValidateV1
	public static final String localhost = "localhost";
	public static final String credPath = "C:\\dbdetail\\Credentials.properties";
	public static final String username = "userName";
	public static final String password = "password";
	public static final String proxyPass = "proxyPassword";
	public static final String serverIp = "serverIp";
	public static final String dbName = "dbName";
	public static final String port = "port";
	public static final String isSSHEnabledUser = "isSSHEnabledUser";
	public static final String directory = "C:\\dbdetail\\Report";

	// For ConnectionDB
	public static final String user = "user";
	public static final String autoReconnect = "autoReconnect";
	public static final String StrictHostKeyChecking = "StrictHostKeyChecking";
	public static final String X11Forwarding = "X11Forwarding";
	public static final String BatchMode = "BatchMode";
	public static final String no = "no";
	public static final String frontslash = "/";
	public static final String colon = ":";
	public static final String jdbc = "jdbc";
	public static final String mysql = "mysql";
	public static final String toArrow = "->";

	// For AllTables
	public static final String False = "False";
	public static final String comma = ",";
	public static final String Data_Summary = "Data_Summary";
	public static final String Data = "Data";
	public static final String Bool = "True/False";
	public static final String tableName = "Table Name";
	public static final String openRoundBraces = "(";
	public static final String closeRoundBraces = ")";
	public static final String Sheet = "Sheet";
	public static final String tablePath = "C:\\dbdetail\\TablesData.properties";

	// For TableCompare
	public static final String No = "No";
	public static final String Yes = "Yes";
	public static final String notPresent = "Not Present";
	public static final String True = "True";
	public static final String falseDataMismatch = "False(Datatype mismatch)";
	public static final String falseFieldNotPresent = "False(Fields not present)";
	public static final String Check = "Check";
	public static final String underscore = "_";
	public static final String naUat = "NA in UAT";
	public static final String UAT = "UAT";
	public static final String naProd = "NA in Prod";
	public static final String Prod = "Prod";
	public static final String whitespace = " ";
	public static final String compare = "Compare";
	public static final String field = "Field";
	public static final String isEqual = "Is Equal";
	public static final String Checker = "Checker";
	public static final String acct_doc_headerrelated = "acct_doc_header_";

	// For GenerateFileName
	public static final String dateFormat = "dd-MM-yyyy HH-mm-ss-SSS";
	public static final String Report = "Report";

	// For DataEvaluate
	public static final String Hyphen = "-";
	public static final String notInProd = "Not in Prod";
	public static final String notInUAT = "Not in UAT";
	public static final String Null = "null";

	// For GenerateExcel
	public static final String backslash = "\\";
	public static final String EXTENSION_XLS = ".xls";
	public static final String EXTENSION_XLSX = ".xlsx";
	public static final String totalConfigTable = "Total Number of config Tables: ";
	public static final String totalDiffConfigTable = "Number of Different config Tables: ";
	public static final String mainSheet = "Back to main sheet";
	public static final String summarySheet = "Summary Sheet";
	public static final String NA = "NA";
	public static final String Different = "Different";
	public static final String colNotInProd = "Column Not in Prod";
	public static final String singleQuote = "'";
	public static final String first = "A1";
	public static final String Exclaim = "!";
	public static final String TripleDot = "...";
	public static final String totalTables = "Total Number of Tables: ";
	public static final String totalDiffTables = "Total Different Tables: ";
	public static final String totalTablesNotPresent = "Total Tables not present: "; 
}
