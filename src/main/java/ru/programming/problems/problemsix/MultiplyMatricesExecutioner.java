package ru.programming.problems.problemsix;

import ru.programming.problems.problemsix.Matrix;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiplyMatricesExecutioner extends Executioner {
    public static void execute(Matrix matrix, ConnectionProvider provider, String tableName) {
        if (matrix == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        int[][] result = matrix.multiplyMatrices();
        matrix.printMatrix(result, "Результат (A x B)");

        String query = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = provider.getConnection().prepareStatement(query)) {
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
