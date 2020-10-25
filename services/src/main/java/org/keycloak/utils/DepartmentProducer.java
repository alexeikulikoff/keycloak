package org.keycloak.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DepartmentProducer {

	private static final String DB_CONFIG = "/etc/keycloak/sys101-keycloak.conf";

	private List<Department> departments;

	public List<Department> findAll() throws SQLException, IOException {

		departments = new ArrayList<>();

		String sql = "select id,short_name from sys101_main.department order by short_name";

		try (InputStream input = new FileInputStream(DB_CONFIG)) {

			Properties cfg = new Properties();

			cfg.load(input);
			String url = cfg.getProperty("db.url");
			Properties props = new Properties();
			props.setProperty("user", cfg.getProperty("db.user"));
			props.setProperty("password", cfg.getProperty("db.password"));

			try (Connection conn = DriverManager.getConnection(url, props)) {
				PreparedStatement preparedStatement = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					String id = resultSet.getString("id");
					String short_name = resultSet.getString("short_name").trim();

					Department dep = new Department(UUID.fromString(id), short_name);
					departments.add(dep);
				}
			}

		}
		return departments;

	}

}
