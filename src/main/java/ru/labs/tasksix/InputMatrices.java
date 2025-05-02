package ru.labs.tasksix;

import ru.labs.tasksix.Matrix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMatrices extends Parent {
    public static Matrix execute(Scanner scanner, Connection conn, String tableName) {
        Matrix matrix = new Matrix();
        try {
            matrix.inputMatricesFromKeyboard();
            matrix.printMatrix(matrix.arrayA, "Первая матрица");
            matrix.printMatrix(matrix.arrayB, "Вторая матрица");

            insertMatrix(conn, matrix.arrayA, tableName, "matrix_1");
            insertMatrix(conn, matrix.arrayB, tableName, "matrix_2");
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только числа.");
            scanner.nextLine();
        }
        return matrix;
    }

    private static void insertMatrix(Connection conn, int[][] matrix, String tableName, String name) {
        String sql = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    stmt.setString(1, name);
                    stmt.setInt(2, i);
                    stmt.setInt(3, j);
                    stmt.setDouble(4, matrix[i][j]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
