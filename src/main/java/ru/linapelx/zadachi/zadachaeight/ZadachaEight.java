package ru.linapelx.zadachi.zadachaeight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ZadachaEight {
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "root";
    protected static final String URL = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    protected static final String TABLE_NAME = "student_salary_table";
    private static final Scanner scanner = new Scanner(System.in);

    private static boolean tableExists = false;

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
                    System.out.println("Ошибка: сначала создайте таблицу (пункт 2).");
                    continue;
                }

                switch (choice) {
                    case 1 -> PrintTables.execute(conn);
                    case 2 -> {
                        CreateTable.execute(conn, TABLE_NAME);
                        tableExists = true;
                    }
                    case 3 -> AddStudent.execute(conn, scanner, TABLE_NAME);
                    case 4 -> SelectAllFromTable.execute(conn, TABLE_NAME);
                    case 5 -> ExportToXls.execute(conn, TABLE_NAME);
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
        System.out.println("3. Ввести данные студента и сохранить в MySQL.");
        System.out.println("4. Вывести данные из MySQL.");
        System.out.println("5. Сохранить данные из MySQL в Excel и вывести их.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
