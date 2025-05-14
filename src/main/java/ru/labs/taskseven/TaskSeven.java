package ru.labs.taskseven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class TaskSeven {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/database?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "elis";
    protected static final String TABLE_NAME = "table_seven";

    protected static final Scanner scanner = new Scanner(System.in);
    private static boolean tableExists = false;
    protected static Sort sort;
    protected static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
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
                    System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).");
                    continue;
                }

                switch (choice) {
                    case 1 -> PrintTables.execute();
                    case 2 -> {
                        tableExists = true;
                        CreateTable.execute();
                    }
                    case 3 -> sort = InputArray.execute();
                    case 4 -> SortArray.execute();
                    case 5 -> ExportToXls.execute();
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
        System.out.println("3. Ввести массив и сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Отсортировать массив, сохранить в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
