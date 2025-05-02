package ru.programming.problems.problemseven;

import ru.programming.problems.problemseven.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ProblemSevenSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_seven_table";

    private static boolean tableExists = false;
    private static Sort sort;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS " + tableName);

            int choice;
            do {
                printMenu();
                while (!scanner.hasNextInt()) {
                    System.out.print("Введите корректный номер действия: ");
                    scanner.next();
                }
                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice >= 3 && choice <= 5 && !tableExists) {
                    System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).\n");
                    continue;
                }

                switch (choice) {
                    case 1 -> PrintTablesExecutioner.execute(conn);
                    case 2 -> {
                        CreateTableExecutioner.execute(conn, tableName);
                        tableExists = true;
                    }
                    case 3 -> sort = InputArrayExecutioner.execute(conn, scanner, tableName);
                    case 4 -> SortArrayExecutioner.execute(conn, sort, tableName);
                    case 5 -> {
                        ExportToCsvExecutioner.execute(conn, tableName);
                        SelectAllFromTableExecutioner.execute(tableName, "id", "array_name", "index_pos", "value");
                    }
                    case -1 -> System.out.println("Выход из программы.");
                    default -> System.out.println("Неверный выбор. Повторите.");
                }
            } while (choice != -1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести массив и сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Отсортировать массив, сохранить в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
