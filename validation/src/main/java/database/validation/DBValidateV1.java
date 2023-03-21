package database.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.Session;

/**
 * <P>
 * 
 * @author anshuman.sarkar
 * @since 1.0
 * @see ConnectionDB
 * @see AllTables
 * @see TableCompare
 */
public class DBValidateV1 {
	private static String userName1 = null;
	private static String password1 = null;
	private static String proxyPassword1 = null;
	private static String serverIp1 = null;
	private static int port1 = 0;
	private static Connection connection1 = null;
	private static Statement statement1 = null;
	private static boolean isSSHEnabledUser1 = false;

	private static String userName2 = null;
	private static String password2 = null;
	private static String proxyPassword2 = null;
	private static String serverIp2 = null;
	private static int port2 = 0;
	private static Connection connection2 = null;
	private static Statement statement2 = null;
	private static boolean isSSHEnabledUser2 = false;

	public static String dbName = null;
	public static List<Session> sessionList = null;

	private static Scanner scanner = null;
//	private static final Logger logger = LogManager.getLogger(DBValidateV1.class);

	/**
	 * Gives the connection and session of the users
	 * <p>
	 * After getting the credentials this functions tries to connect the user with
	 * the database and if the isSSHEnabledUser is true for any user it will do the
	 * SSH connection first then it will go for the normal database connection.
	 * 
	 * @return session
	 * @since 1.0
	 */
	protected static LinkedList<Session> joinConnection() {
		LinkedList<Session> session = new LinkedList<Session>();
		System.out.println("User1: connecting");
		try {
			if (isSSHEnabledUser1) {
				try {
					session.add(ConnectionDB.sshConnection(userName1, password1, serverIp1, port1));
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				connection1 = ConnectionDB.getConnection(userName1, proxyPassword1, StringConstants.localhost, port1,
						dbName);
			} else
				connection1 = ConnectionDB.getConnection(userName1, password1, serverIp1, port1, dbName);
			System.out.println("User1:Connected Successfully");
			System.out.println("User2: connecting");
			if (isSSHEnabledUser2) {
				try {
					session.add(ConnectionDB.sshConnection(userName2, password2, serverIp2, port2));
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				connection2 = ConnectionDB.getConnection(userName2, proxyPassword2, StringConstants.localhost, port2,
						dbName);
			} else
				connection2 = ConnectionDB.getConnection(userName2, password2, serverIp2, port2, dbName);
			System.out.println("User2:Connected Successfully");

			statement1 = (Statement) connection1.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			statement2 = (Statement) connection2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			e.printStackTrace();
			closeConnection();
		}
		return session;

	}

	/**
	 * Closes the connection with the session if any.
	 */
	protected static void closeConnection() {
		if (sessionList != null) {
			for (Iterator<Session> iterator = sessionList.iterator(); iterator.hasNext();) {
				Session session = (Session) iterator.next();
				if (session != null) {
					session.disconnect();
				}
			}
		}
		try {
			if (connection1 != null) {
				connection1.close();
			}
			if (connection2 != null) {
				connection2.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Terminating!!!!!!");
			long endTime = System.nanoTime();
			System.out.println(TimeUnit.NANOSECONDS.toSeconds(endTime - startTime) + " seconds");
			System.exit(0);
		}
	}

	protected static long startTime = System.nanoTime();

	protected static void agentRun() {

		HashMap<String, LinkedList<String>> hmArray = new HashMap<>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(StringConstants.credPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		hmArray = GetDetailsFromFile.getDetailsFromProperties(fis);
		System.out.println("Setting Credentials:");
		/**
		 * Contains the credentials required for the agent to run
		 */
		dbName = hmArray.get(StringConstants.dbName).get(0);

		System.out.println("For User1: ");
		{
			userName1 = hmArray.get(StringConstants.username).get(0);
			password1 = hmArray.get(StringConstants.password).get(0);
			proxyPassword1 = hmArray.get(StringConstants.proxyPass).get(0);
			serverIp1 = hmArray.get(StringConstants.serverIp).get(0);
			port1 = Integer.valueOf(hmArray.get(StringConstants.port).get(0));
			isSSHEnabledUser1 = Boolean.parseBoolean(hmArray.get(StringConstants.isSSHEnabledUser).get(0));
		}
		System.out.println("User1 Completed");

		System.out.println("For User2: ");
		{
			userName2 = hmArray.get(StringConstants.username).get(1);
			password2 = hmArray.get(StringConstants.password).get(1);
			proxyPassword2 = hmArray.get(StringConstants.proxyPass).get(1);
			serverIp2 = hmArray.get(StringConstants.serverIp).get(1);
			port2 = Integer.valueOf(hmArray.get(StringConstants.port).get(1));
			isSSHEnabledUser2 = Boolean.parseBoolean(hmArray.get(StringConstants.isSSHEnabledUser).get(1));
		}
		System.out.println("User2 Completed");

		String directory = StringConstants.directory;
		File file = new File(directory);
		if (!file.exists()) {
			System.out.println("Creating directory " + directory + " as no such directory is present in the device!");
			file.mkdir();
		} else {
			System.out.println(file.delete());
			file.mkdir();
		}

		sessionList = joinConnection();

		try {
//			AllTables.dataCompare(statement1, statement2, dbName);
//			TableCompare.getAllTables(statement1, statement2, dbName);

			scanner = new Scanner(System.in);
			System.out.println("What Do you want to do First?\nData Check (1) or Type Check (2)");
			int choice = (scanner == null) ? 3 : scanner.nextInt();
			Character ch = null;
			switch (choice) {
			case 1:
				System.out.println("\n\nDoing Data Compare\n\n");
				AllTables.dataCompare(statement1, statement2, dbName);
				System.out.println("Do you want to do Type compare? (Y/N)");
				ch = scanner.next().charAt(0);
				scanner.close();
				if (ch == 'Y' || ch == 'y') {
					System.out.println("\n\nDoing Type Compare\n\n");
					TableCompare.getAllTables(statement1, statement2, dbName);
				}
				System.out.println("The generated file is in : " + StringConstants.directory + " path location!");
				break;
			case 2:
				TableCompare.getAllTables(statement1, statement2, dbName);
				System.out.println("Do you want to do Data compare? (Y/N)");
				ch = scanner.next().charAt(0);
				scanner.close();
				if (ch == 'Y' || ch == 'y') {
					System.out.println("\n\nDoing Data Compare\n\n");
					AllTables.dataCompare(statement1, statement2, dbName);
				}
				System.out.println("The generated file is in : " + StringConstants.directory + " path location!");
				break;

			default:
				System.out.println("Wrong Input! Terminating the program...\nReason: Your input is " + choice);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public static void main(String[] args) {
		agentRun();
	}

}