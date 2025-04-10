package ru.labs.task6;

import ru.labs.DbHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskSixRunner {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "lab6_matrix";
    private static boolean isTableCreated = false;

    private static Matrix matrix;

    public static void main(String[] args) {
        DbHelper.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
        isTableCreated = false;

        int userChoice;
        do {
            displayMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            userChoice = scanner.nextInt();
            scanner.nextLine();
            handleAction(userChoice);
        } while (userChoice != -1);
    }

    private static void displayMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить перемноженную матрицу в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
        System.out.println("Для выхода из программы введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void handleAction(int choice) {
        if (choice >= 3 && choice <= 5 && !isTableCreated) {
            System.out.println("Ошибка, таблица не создана.");
            return;
        }

        switch (choice) {
            case 1 -> DbHelper.listTables();
            case 2 -> {
                DbHelper.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "matrix_name VARCHAR(255), " +
                        "row_index INT, " +
                        "col_index INT, " +
                        "value DOUBLE)");
                isTableCreated = true;
                System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
            }
            case 3 -> {
                try {
                    matrix = new Matrix();
                    matrix.inputMatricesFromKeyboard();

                    matrix.printMatrix(matrix.arrayA, "Первая матрица:");
                    matrix.printMatrix(matrix.arrayB, "Вторая матрица:");

                    insertMatrix(matrix.arrayA, "matrix_A");
                    insertMatrix(matrix.arrayB, "matrix_B");
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода: необходимо вводить только числа. Повторите снова.");
                    scanner.nextLine();
                }
            }
            case 4 -> {
                if (matrix == null) {
                    System.out.println("Ошибка: матрицы не были введены.");
                    return;
                }
                int[][] result = matrix.multiplyMatrices();
                matrix.printMatrix(result, "Результат (A x B)");
                insertMatrix(result, "matrix_C");
            }
            case 5 -> {
                DbHelper.exportToCsv(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                DbHelper.selectAllFromTable(TABLE_NAME, "id", "matrix_name", "row_index", "col_index", "value");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void insertMatrix(int[][] matrixData, String name) {
        String sql = "INSERT INTO " + TABLE_NAME + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        for (int i = 0; i < matrixData.length; i++) {
            for (int j = 0; j < matrixData[i].length; j++) {
                DbHelper.execute(sql, name, i, j, matrixData[i][j]);
            }
        }
    }
}
