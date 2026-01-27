package com.revhire.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("Initializing database...");
        try (Connection conn = DBConnectionManager.getConnection();
                Statement stmt = conn.createStatement()) {

            // Load schema.sql from classpath
            InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql");
            if (inputStream == null) {
                System.err.println("schema.sql not found in resources!");
                return;
            }

            String schemaSql = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));

            // Split by semicolon to execute queries individually because JDBC Statement
            // doesn't support multiple queries by default usually
            // However, for simple script runners, we might need a parser.
            // A simple split by ";" might break if there are semicolons in strings, but
            // schema.sql looks simple.
            // Let's try executing the whole script if the driver supports it, or split.
            // Safer to split for generic JDBC.

            String[] queries = schemaSql.split(";");

            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    try {
                        stmt.execute(query);
                    } catch (Exception e) {
                        System.out.println("Query failed: " + query.trim());
                        System.out.println("Reason: " + e.getMessage());
                    }
                }
            }
            System.out.println("Database initialization completed.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database initialization failed.");
        }
    }
}
