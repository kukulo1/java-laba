package ru.linapelx.zadachi.zadachasix;

import ru.linapelx.zadachi.zadachasix.Matrix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMatrices extends Command {
    public static Matrix execute(Connection conn, Scanner scanner, String tableName) {
        Matrix matrix = new Matrix();
        try {
            matrix.inputMatricesFromKeyboard();
            matrix.printMatrix(matrix.arrayA, "Первая матрица");
            matrix.printMatrix(matrix.arrayB, "Вторая матрица");

            insertMatrix(conn, matrix.arrayA, tableName, "matrix_1");
            insertMatrix(conn, matrix.arrayB, tableName, "matrix_2");

        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: только числа.");
            scanner.nextLine();
        } catch (SQLException e) {
            System.out.println("Ошибка при запросе к бд! Попробуйтен еще раз");
        }

        return matrix;
    }

    private static void insertMatrix(Connection conn, int[][] matrix, String tableName, String name) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    stmt.setString(1, name);
                    stmt.setInt(2, i);
                    stmt.setInt(3, j);
                    stmt.setDouble(4, matrix[i][j]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}
