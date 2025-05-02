package ru.linapelx.zadachi.zadachaseven;

import ru.linapelx.zadachi.zadachaseven.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ZadachaSeven {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "kukulo1";
    private static final String URL = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String TABLE_NAME = "array_sort_table";

    private static final Scanner scanner = new Scanner(System.in);
    private static boolean tableExists = false;
    private static Sort sort;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            tableExists = false;

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
                    case 1 -> PrintTables.execute(conn);
                    case 2 -> {
                        CreateTable.execute(conn, TABLE_NAME);
                        tableExists = true;
                    }
                    case 3 -> sort = InputArray.execute(conn, scanner, TABLE_NAME);
                    case 4 -> SortArray.execute(conn, TABLE_NAME, sort);
                    case 5 -> ExportToCsv.execute(conn, TABLE_NAME);
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
