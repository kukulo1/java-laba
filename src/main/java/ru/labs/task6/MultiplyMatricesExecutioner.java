package ru.labs.task6;

import ru.labs.DbHelper;

public class MultiplyMatricesExecutioner extends TaskSixRunner{
    public static void execute() {
        if (matrix.arrayA == null || matrix.arrayB == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        int[][] result = matrix.multiplyMatrices();
        matrix.printMatrix(result, "Результат (A x B)");

        String sql = "INSERT INTO " + TABLE_NAME + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                DbHelper.execute(sql, "matrix_C", i, j, result[i][j]);
            }
        }
    }
}
