package ru.programming.problems.problemeight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class ProblemEightSolver {
    protected static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    protected static final String tableName = "problem_eight_table";
    private static final List<Worker> workers = new ArrayList<>();
    private static boolean tableExists = false;
    protected static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(url, username, password);
        }catch (SQLException e) {
            e.printStackTrace();
        }
        try {
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
                    System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).");
                    continue;
                }

                switch (choice) {
                    case 1 -> PrintTablesExecutioner.execute();
                    case 2 -> {
                        CreateTableExecutioner.execute();
                        tableExists = true;
                    }
                    case 3 -> InsertWorkerExecutioner.execute(workers);
                    case 4 -> SelectAllFromTableExecutioner.execute();
                    case 5 -> ExportToXlsExecutioner.execute();
                    case -1 -> System.out.println("Выход из программы.");
                    default -> {
                        System.out.println("Неверный выбор. Повторите.");
                    }
                }

            } while (choice != -1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввод значений ВСЕХ полей, сохранить их в MySQL с выводом в консоль.");
        System.out.println("4. Сохранение всех результатов из MySQL с последующим выводом в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }
}
