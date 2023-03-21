package database.validation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Does the connection to the server for user
 * <p>
 * When called, First if the user is set for SSH connection it will do the SSH
 * connection using the JSch class and after doing so it does the normal
 * Database connection using the mysql driver.
 * <p>
 * If there is any issue during the connection it will automatically close the
 * connections (if done any) and will successfully terminate the program.
 * 
 * @author anshuman.sarkar
 * @see DBValidateV1
 * @since 1.0
 */
public class ConnectionDB {
	/**
	 * log variable for the generation of the log.
	 */
//	private static final Logger log = LogManager.getLogger(ConnectionDB.class);

	/**
	 * Does the connection to the Database.
	 * 
	 * @param userName   Username of the user which lets the user to log in.
	 * @param password   Password given by the user which lets the user to log in.
	 * @param serverIp   The Ip Address to the database.
	 * @param portNumber The port of the database.
	 * @param dbName     Name of the Database.
	 * @return connection for the user
	 * @since 1.0
	 */
	protected static Connection getConnection(String userName, String password, String serverIp, int portNumber,
			String dbName) {
		Connection connection = null;
		Properties connectionProps = new Properties();
		connectionProps.put(StringConstants.user, userName);
		connectionProps.put(StringConstants.password, password);
		connectionProps.put(StringConstants.autoReconnect, true);
		DriverManager.setLoginTimeout(2000000000);
		final String url = StringConstants.jdbc + StringConstants.colon + StringConstants.mysql + StringConstants.colon
				+ StringConstants.frontslash + StringConstants.frontslash + serverIp + StringConstants.colon
				+ portNumber + StringConstants.frontslash + dbName;
		try {
			connection = DriverManager.getConnection(url, connectionProps);
		} catch (SQLException e) {
//			log.error("Database access error", e);
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
		return connection;
	}

	/**
	 * Does the SSH connection if needed.
	 * 
	 * @param user       username of the user
	 * @param password   password of the user
	 * @param portNumber port number of the database we want to connect
	 * @param serverIp   Ip address of the Database.
	 * @return session for the user
	 * @since 1.0
	 */
	protected static Session sshConnection(String user, String password, String serverIp, int portNumber) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(user, serverIp, 22);
		} catch (JSchException e) {
//			log.error("Username or serverIp is invalid. Please check and try again later.", e);
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
		session.setPassword(password);
		session.setConfig(StringConstants.StrictHostKeyChecking, StringConstants.no);
		session.setConfig(StringConstants.X11Forwarding, StringConstants.no);
		session.setConfig(StringConstants.BatchMode, StringConstants.no);
//		log.info("Establishing Connection...");
		System.out.println("Establishing Connection...");
		try {
			session.connect();
		} catch (JSchException e) {
//			log.error("Session is already connected", e);
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
		try {
			int assinged_port_L = session.setPortForwardingL(StringConstants.localhost, portNumber, serverIp,
					portNumber);
//			log.info(StringConstants.localhost + StringConstants.colon + assinged_port_L + StringConstants.whitespace + StringConstants.toArrow + StringConstants.whitespace + serverIp + StringConstants.colon + portNumber);
			System.out.println(StringConstants.localhost + StringConstants.colon + assinged_port_L
					+ StringConstants.whitespace + StringConstants.toArrow + StringConstants.whitespace + serverIp
					+ StringConstants.colon + portNumber);
		} catch (JSchException e) {
//			log.error("Connection error", e);
			e.printStackTrace();
			DBValidateV1.closeConnection();
		}
		return session;
	}

}
