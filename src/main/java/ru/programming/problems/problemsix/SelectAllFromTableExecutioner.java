package ru.programming.problems.problemsix;

import java.sql.*;

public class SelectAllFromTableExecutioner extends ProblemSixSolver{
    public static void execute() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-3s | %-15s | %-10s | %-10s | %-20s\n",
                    "ID", "matrix name", "row index", "column index", "matrix_value");


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
