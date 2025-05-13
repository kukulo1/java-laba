package ru.labs.task6;

import ru.labs.DbHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMatricesExecutioner extends TaskSixRunner {
    public static void execute() {
        try {
            matrix.inputMatricesFromKeyboard();
            matrix.printMatrix(matrix.arrayA, "Первая матрица:");
            matrix.printMatrix(matrix.arrayB, "Вторая матрица:");
            insertMatrix(matrix.arrayA, "matrix_A", TABLE_NAME);
            insertMatrix(matrix.arrayB, "matrix_B", TABLE_NAME);
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только числа. Повторите снова.");
            scanner.nextLine();
        }
    }

    private static void insertMatrix(int[][] matrix, String name, String tableName) {
        String sql = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                DbHelper.execute(sql, name, i, j, matrix[i][j]);
            }
        }
    }
}
