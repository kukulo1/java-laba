package ru.linapelx.zadachi;

import java.io.*;
import java.sql.*;
import java.util.*;

public class ZadachaTwo {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String TABLE = "string_task_2_set_b";
    private static final Scanner scanner = new Scanner(System.in);
    private static String lineA = null;
    private static String lineB = null;
    private static boolean tableCreated = false;

    public static void main(String[] args) {
        executeSQL("DROP TABLE IF EXISTS " + TABLE);
        tableCreated = false;

        int menuChoice;
        do {
            showMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите номер действия: ");
                scanner.next();
            }
            menuChoice = scanner.nextInt();
            scanner.nextLine();
            handle(menuChoice);
        } while (menuChoice != -1);
    }

    private static void showMenu() {
        System.out.println("1. Показать все таблицы в БД");
        System.out.println("2. Создать таблицу в БД");
        System.out.println("3. Ввод строк с клавиатуры и сохранение");
        System.out.println("4. Подсчитать длины строк");
        System.out.println("5. Объединить строки");
        System.out.println("6. Сравнить строки");
        System.out.println("7. Сохранить всё в Excel и вывести в консоль");
        System.out.println("-1. Выход");
        System.out.print("Выбор: ");
    }

    private static void handle(int code) {
        if (code > 2 && code <= 7 && !tableCreated) {
            System.out.println("Сначала необходимо создать таблицу (пункт 2).");
            return;
        }

        switch (code) {
            case 1:
                listTables();
                break;
            case 2:
                createMainTable();
                tableCreated = true;
                break;
            case 3:
                lineA = inputString("Введите первую строку (>= 50 симв.): ");
                lineB = inputString("Введите вторую строку (>= 50 симв.): ");
                executeSQL(insertStatement(), "INPUT", lineA, lineB, null);
                System.out.println("Строки сохранены.");
                break;
            case 4:
                if (!stringsReady()) return;
                String lenResult = "A: " + lineA.length() + ", B: " + lineB.length();
                executeSQL(insertStatement(), "LENGTHS", lineA, lineB, lenResult);
                System.out.println("Длины строк: " + lenResult);
                break;
            case 5:
                if (!stringsReady()) return;
                String merged = lineA + lineB;
                executeSQL(insertStatement(), "MERGE", lineA, lineB, merged);
                System.out.println("Склейка: " + merged);
                break;
            case 6:
                if (!stringsReady()) return;
                String cmp = lineA.equals(lineB) ? "equal" : "not equal";
                executeSQL(insertStatement(), "COMPARE", lineA, lineB, cmp);
                System.out.println("Сравнение: " + cmp);
                break;
            case 7:
                exportToCSV(TABLE, TABLE);
                System.out.println("Экспорт завершён.");
                printTableContent(TABLE);
                break;
            case -1:
                System.out.println("Завершение работы.");
                break;
            default:
                System.out.println("Некорректный выбор.");
        }
    }

    private static String inputString(String prompt) {
        String text;
        do {
            System.out.print(prompt);
            text = scanner.nextLine();
            if (text.length() < 50) {
                System.out.println("Минимум 50 символов!");
            }
        } while (text.length() < 50);
        return text;
    }

    private static boolean stringsReady() {
        if (lineA == null || lineB == null) {
            System.out.println("Сначала введите строки (пункт 3).");
            return false;
        }
        return true;
    }

    private static void createMainTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(100), operand1 TEXT, operand2 TEXT, result TEXT)";
        executeSQL(sql);
        System.out.println("Таблица создана: " + TABLE);
    }

    private static String insertStatement() {
        return "INSERT INTO " + TABLE + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }

    private static void executeSQL(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("Существующие таблицы:");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportToCSV(String tableName, String fileAlias) {
        String path = "src/main/resources/" + fileAlias + ".csv";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             FileWriter fw = new FileWriter(path)) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                fw.write('"' + md.getColumnName(i) + '"');
                if (i < cols) fw.write(";");
            }
            fw.write("\n");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String val = rs.getString(i);
                    fw.write('"' + (val != null ? val.replace("\"", "\"\"") : "") + '"');
                    if (i < cols) fw.write(";");
                }
                fw.write("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printTableContent(String table) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                System.out.print(md.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}