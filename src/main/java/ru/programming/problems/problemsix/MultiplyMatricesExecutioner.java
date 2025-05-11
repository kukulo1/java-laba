package ru.programming.problems.problemsix;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiplyMatricesExecutioner extends ProblemSixSolver{
    public static void execute() {
        if (matrix == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        long[][] result = matrix.multiplyMatrices();
        matrix.printMatrix(result, "Результат (A x B)");

        String query = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    stmt.setString(1, "result");
                    stmt.setInt(2, i);
                    stmt.setInt(3, j);
                    stmt.setLong(4, result[i][j]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
