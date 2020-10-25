package org.keycloak.authentication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.jboss.logging.Logger;

public class CustomRoleModelAuthenticationFlow {

	private static final Logger logger = Logger.getLogger(CustomRoleModelAuthenticationFlow.class);

	private static final String DB_CONFIG = "/etc/keycloak/sys101-keycloak.conf";

	public void doRoleModelAuth(String username, String depertmentId) {

		String sql = "select * from sys101_main.user join sys101_main.user_role on sys101_main.user_role.userid = sys101_main.user.id join  sys101_main.role_department on  sys101_main.role_department.roleid = sys101_main.user_role.roleid where sys101_main.role_department.departmentid  = '"
				+ depertmentId + "' and sys101_main.user.name = '" + username + "'";

		try (InputStream input = new FileInputStream(DB_CONFIG)) {
			Properties cfg = new Properties();
			try {
				cfg.load(input);
				String url = cfg.getProperty("db.url");
				Properties props = new Properties();
				props.setProperty("user", cfg.getProperty("db.user"));
				props.setProperty("password", cfg.getProperty("db.password"));
				try (Connection conn = DriverManager.getConnection(url, props)) {
					PreparedStatement preparedStatement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					ResultSet resultSet = preparedStatement.executeQuery();
					if (resultSet.next() == false) {
						throw new AuthenticationFlowException(AuthenticationFlowError.UNKNOWN_USER);
					}
				} catch (SQLException e) {

					logger.error("Error has occured while connecting to role-model database: " + e.getMessage());

					throw new AuthenticationFlowException(AuthenticationFlowError.UNKNOWN_USER);
				}

			} catch (IOException e) {

				logger.error("Error has occured while loading config file for role-model database: " + e.getMessage());

				throw new AuthenticationFlowException(AuthenticationFlowError.UNKNOWN_USER);

			}

		} catch (IOException e) {

			logger.error("Error has occured while opening config file for role-model database: " + e.getMessage());

			throw new AuthenticationFlowException(AuthenticationFlowError.UNKNOWN_USER);
		}

	}
}
