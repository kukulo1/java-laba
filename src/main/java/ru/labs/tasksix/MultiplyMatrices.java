package ru.labs.tasksix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiplyMatrices extends TaskSix {
    public static void execute() {
        if (matrix == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        int[][] result = matrix.multiplyMatrices();
        matrix.printMatrix(result, "Результат (A x B)");

        String sql = "INSERT INTO " + TABLE_NAME + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    stmt.setString(1, "resultOfMultiplication");
                    stmt.setInt(2, i);
                    stmt.setInt(3, j);
                    stmt.setDouble(4, result[i][j]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
