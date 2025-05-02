package ru.programming.problems.problemsix;

import ru.programming.problems.problemsix.*;

import java.util.Scanner;

public class ProblemSixSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String tableName = "problem_six_table";
    private static final ConnectionProvider connectionProvider =
            new ConnectionProvider("jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true", "root", "kukulo1");

    private static boolean tableCreated = false;
    private static Matrix matrix;

    public static void main(String[] args) {
        try {
            connectionProvider.getConnection().createStatement().execute("DROP TABLE IF EXISTS " + tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int choice;
        do {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 3 && choice <= 5 && !tableCreated) {
                System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).\n");
                continue;
            }

            switch (choice) {
                case 1 -> PrintTablesExecutioner.execute(connectionProvider);
                case 2 -> {
                    CreateTableExecutioner.execute(connectionProvider, tableName);
                    tableCreated = true;
                }
                case 3 -> matrix = InputMatricesExecutioner.execute(scanner, connectionProvider, tableName);
                case 4 -> MultiplyMatricesExecutioner.execute(matrix, connectionProvider, tableName);
                case 5 -> ExportToCsvExecutioner.execute(connectionProvider, tableName);
                case -1 -> System.out.println("Выход из программы.");
                default -> System.out.println("Неверный выбор. Повторите.");
            }
        } while (choice != -1);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить результат в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
