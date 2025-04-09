package ru.linapelx.zadachi.zadachaeight;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ZadachaEight {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String tableName = "student_salary_table";
    private static boolean tableExists = false;
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
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
            execute(choice);
        } while (choice != -1);
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

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 5 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2).");
            return;
        }

        switch (choice) {
            case 1 -> printTables();
            case 2 -> {
                createTable();
                tableExists = true;
            }
            case 3 -> addWorker();
            case 4 -> selectAllFromTable();
            case 5 -> {
                exportToCsv();
                selectAllFromTable();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void addWorker() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        int age = -1;
        while (age < 0 || age > 100) {
            System.out.print("Введите возраст (0-100): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ошибка. Введите целое число: ");
                scanner.next();
            }
            age = scanner.nextInt();
        }

        double salary = -1;
        while (salary < 0) {
            System.out.print("Введите зарплату: ");
            while (!scanner.hasNextDouble()) {
                System.out.print("Ошибка. Введите число: ");
                scanner.next();
            }
            salary = scanner.nextDouble();
        }
        scanner.nextLine();

        executeStatement("INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)", name, age, salary);
        System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n", name, age, salary);
    }

    private static void createTable() {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "age INT, " +
                "salary DOUBLE)");
        System.out.println("Таблица успешно создана.");
    }

    private static void executeStatement(String query, Object... params) {
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printTables() {
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void selectAllFromTable() {
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportToCsv() {
        String filePath = "src/main/resources/" + tableName + ".csv";
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter fw = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                fw.append('"').append(meta.getColumnName(i)).append('"');
                if (i < columnCount) fw.append(";");
            }
            fw.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    fw.append('"');
                    if (value != null) fw.append(value.replace("\"", "\"\""));
                    fw.append('"');
                    if (i < columnCount) fw.append(";");
                }
                fw.append("\n");
            }

            System.out.println("CSV файл успешно сохранён: " + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
