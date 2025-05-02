package ru.labs.task6;

import ru.labs.DbHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputMatricesExecutioner extends Executioner{
    public static void execute(Scanner scanner, String tableName, Matrix matrixContainer) {
        try {
            matrixContainer.inputMatricesFromKeyboard();
            matrixContainer.printMatrix(matrixContainer.arrayA, "Первая матрица:");
            matrixContainer.printMatrix(matrixContainer.arrayB, "Вторая матрица:");
            insertMatrix(matrixContainer.arrayA, "matrix_A", tableName);
            insertMatrix(matrixContainer.arrayB, "matrix_B", tableName);
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
