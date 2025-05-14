package ru.labs.taskseven;

import java.sql.*;

public class SelectAllFromTable extends TaskSeven {
    public static void execute() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
