package ru.labs.tasksix;

import ru.labs.tasksix.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class TaskSix {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/database?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "table_six";

    private static boolean tableExists = false;
    private static Matrix matrix;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            tableExists = false;

            int choice;
            do {
                showConsoleMenu();
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
                    case 1 -> ru.labs.tasksix.menu.PrintTables.execute(conn);
                    case 2 -> {
                        ru.labs.tasksix.menu.CreateTable.execute(conn, TABLE_NAME);
                        tableExists = true;
                    }
                    case 3 -> matrix = InputMatrices.execute(scanner, conn, TABLE_NAME);
                    case 4 -> MultiplyMatrices.execute(conn, TABLE_NAME, matrix);
                    case 5 -> ExportToCsv.execute(conn, TABLE_NAME);
                    case -1 -> System.out.println("Выход из программы.");
                    default -> System.out.println("Неверный выбор. Повторите.");
                }

            } while (choice != -1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showConsoleMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить результат в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
