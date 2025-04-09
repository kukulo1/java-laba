package ru.linapelx.zadachi;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class ZadachaThree {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DB_URL = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String TABLE_NAME = "int_check_task_3_b";
    private static boolean tableWasCreated = false;

    public static void main(String[] args) {
        clearTableIfExists();
        int choice = 0;

        while (choice != -1) {
            showMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            handleChoice(choice);
        }
    }

    private static void showMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void clearTableIfExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleChoice(int option) {
        if (option >= 3 && option <= 4 && !tableWasCreated) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).\n");
            return;
        }

        switch (option) {
            case 1:
                listTables();
                break;
            case 2:
                createTable();
                tableWasCreated = true;
                break;
            case 3:
                checkNumbers();
                break;
            case 4:
                exportTableToCSV();
                displayTable();
                break;
            case -1:
                System.out.println("Выход из программы.");
                break;
            default:
                System.out.println("Неверный выбор. Повторите.\n");
        }
    }

    private static void listTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            boolean found = false;
            while (rs.next()) {
                System.out.println(rs.getString(1));
                found = true;
            }
            if (!found) System.out.println("Таблицы не найдены.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "operation VARCHAR(100), "
                + "operand1 VARCHAR(50), "
                + "operand2 VARCHAR(50), "
                + "result VARCHAR(50))";
        runUpdate(query);
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!\n");
    }

    private static void checkNumbers() {
        System.out.println("Введите числа через пробел (для завершения введите 'q'):");
        while (true) {
            String input = scanner.next();
            if (input.equalsIgnoreCase("q")) break;

            try {
                double val = Double.parseDouble(input);
                if (val % 1 != 0) {
                    System.out.println(input + " — нецелое число");
                    insertIntoTable("NotInteger", input, "", "Invalid");
                } else {
                    int intVal = (int) val;
                    boolean isEven = intVal % 2 == 0;
                    String result = isEven ? "Even" : "Odd";
                    System.out.println(intVal + " — целое " + (isEven ? "четное" : "нечетное") + " число");
                    insertIntoTable("Check", String.valueOf(intVal), "", result);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: '" + input + "' не является числом");
                insertIntoTable("NotNumber", input, "", "Invalid");
            }
        }
    }

    private static void insertIntoTable(String operation, String op1, String op2, String result) {
        String sql = "INSERT INTO " + TABLE_NAME + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, operation);
            pstmt.setString(2, op1);
            pstmt.setString(3, op2);
            pstmt.setString(4, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayTable() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportTableToCSV() {
        String path = "src/main/resources/" + TABLE_NAME + ".csv";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
             FileWriter fw = new FileWriter(path)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                fw.append(meta.getColumnName(i)).append(i < cols ? ";" : "\n");
            }

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    fw.append(rs.getString(i)).append(i < cols ? ";" : "\n");
                }
            }
            System.out.println("Данные успешно экспортированы в файл CSV: " + path);

        } catch (SQLException | IOException e) {
            System.out.println("Ошибка при экспорте данных в CSV файл.");
            e.printStackTrace();
        }
    }

    private static void runUpdate(String sql) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}