package ru.programming.problems.problemsix;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class InputMatricesExecutioner extends ProblemSixSolver{
    public static Matrix execute() {
        Matrix matrix = new Matrix();
        try {
            matrix.inputMatricesFromKeyboard();
            matrix.printMatrix(matrix.arrayA, "Первая матрица");
            matrix.printMatrix(matrix.arrayB, "Вторая матрица");
            insertMatrix(matrix.arrayA, "matrix_1");
            insertMatrix(matrix.arrayB, "matrix_2");
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только числа.");
            scanner.nextLine();
        }
        return matrix;
    }

    private static void insertMatrix(long[][] data, String name) {
        String query = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    stmt.setString(1, name);
                    stmt.setInt(2, i);
                    stmt.setInt(3, j);
                    stmt.setLong(4, data[i][j]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
