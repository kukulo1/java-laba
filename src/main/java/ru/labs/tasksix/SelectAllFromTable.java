package ru.labs.tasksix;

import java.sql.*;

public class SelectAllFromTable extends Parent {
    public static void execute(Connection conn, String tableName, String... columns) {
        String query = (columns != null && columns.length > 0)
                ? "SELECT " + String.join(", ", columns) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
