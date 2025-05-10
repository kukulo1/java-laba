package ru.linapelx.zadachi.zadachasix;

import java.sql.*;

public class SelectAllFromTable extends ZadachaSix {
    public static void execute(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-3s | %-15s | %-10s | %-10s | %-20s\n",
                    "ID", "Matrix Name", "Row Index", "Col Index", "Value");


            while (rs.next()) {
                int id = rs.getInt("id");
                String matrixName = rs.getString("matrix_name");
                int rowIndex = rs.getInt("row_index");
                int colIndex = rs.getInt("col_index");
                long value = rs.getLong("value");

                System.out.printf("%-3d | %-15s | %-10d | %-10d | %-20d\n",
                        id, matrixName, rowIndex, colIndex, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
