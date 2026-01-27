package com.revhire.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    // Port 3307 as requested
    // Added useSSL=false, allowPublicKeyRetrieval=true, serverTimezone=UTC
    private static final String URL = "jdbc:mysql://127.0.0.1:3307/revhire_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Ranjeet@123"; // Use your actual MySQL password

    private static Connection connection;

    private DBConnectionManager() {
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
