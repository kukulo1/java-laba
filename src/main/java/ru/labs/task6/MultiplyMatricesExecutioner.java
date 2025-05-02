package ru.labs.task6;

import ru.labs.DbHelper;

public class MultiplyMatricesExecutioner extends Executioner{
    public static void execute(String tableName, Matrix matrixContainer) {
        if (matrixContainer.arrayA == null || matrixContainer.arrayB == null) {
            System.out.println("Ошибка: матрицы не были введены.");
            return;
        }

        int[][] result = matrixContainer.multiplyMatrices();
        matrixContainer.printMatrix(result, "Результат (A x B)");

        String sql = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                DbHelper.execute(sql, "matrix_C", i, j, result[i][j]);
            }
        }
    }
}
