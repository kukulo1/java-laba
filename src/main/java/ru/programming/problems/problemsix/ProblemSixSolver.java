package ru.programming.problems.problemsix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class ProblemSixSolver {
    protected static final Scanner scanner = new Scanner(System.in);
    protected static final String tableName = "problem_six_table";
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    private static boolean tableCreated = false;
    protected static Matrix matrix;
    protected static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int choice;
        do {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.println("Неверный выбор. Повторите.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 3 && choice <= 5 && !tableCreated) {
                System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).\n");
                continue;
            }

            switch (choice) {
                case 1 -> PrintTablesExecutioner.execute();
                case 2 -> {
                    CreateTableExecutioner.execute();
                    tableCreated = true;
                }
                case 3 -> matrix = InputMatricesExecutioner.execute();
                case 4 -> MultiplyMatricesExecutioner.execute();
                case 5 -> ExportToXlsExecutioner.execute();
                case -1 -> System.out.println("Выход из программы.");
                default -> {
                    System.out.println("Неверный выбор. Повторите.");
                }
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
