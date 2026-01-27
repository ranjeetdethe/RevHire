package com.revhire.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class SchemaFixer {
    public static void fixSchema() {
        System.out.println("Running SchemaFixer...");
        try (Connection conn = DBConnectionManager.getConnection();
                Statement stmt = conn.createStatement()) {

            String[] columns = { "education", "experience", "skills", "certifications", "location" };

            for (String col : columns) {
                if (!columnExists(conn, "job_seekers", col)) {
                    System.out.println("Adding missing column: " + col);
                    try {
                        stmt.execute("ALTER TABLE job_seekers ADD COLUMN " + col + " VARCHAR(255)");
                        System.out.println("Column " + col + " added successfully.");
                    } catch (SQLException e) {
                        System.err.println("Failed to add column " + col + ": " + e.getMessage());
                    }
                } else {
                    System.out.println("Column " + col + " already exists.");
                }
            }

            // Also ensure resume_text exists (it was in original but good to verify)
            if (!columnExists(conn, "job_seekers", "resume_text")) {
                stmt.execute("ALTER TABLE job_seekers ADD COLUMN resume_text TEXT");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
