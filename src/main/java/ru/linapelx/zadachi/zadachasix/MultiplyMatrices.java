package ru.linapelx.zadachi.zadachasix;

import ru.linapelx.zadachi.zadachasix.Matrix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MultiplyMatrices extends Command {
    public static void execute(Connection conn, String tableName, Matrix matrix) {
        if (matrix == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        int[][] result = matrix.multiplyMatrices();
        matrix.printMatrix(result, "Результат (A x B)");

        String sql = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[i].length; j++) {
                    stmt.setString(1, "result_matrix");
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
