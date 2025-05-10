package ru.linapelx.zadachi;

import java.sql.*;
import java.util.*;

public class ZadachaTwo {

    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    private static final String tableName = "string_task_2_set_b";
    private static final Scanner scanner = new Scanner(System.in);
    private static String lineA = null;
    private static String lineB = null;
    private static boolean tableNameCreated = false;

    public static void main(String[] args) {
        executeSQL("DROP TABLE IF EXISTS " + tableName);
        tableNameCreated = false;

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
        if (code > 2 && code <= 7 && !tableNameCreated) {
            System.out.println("Сначала необходимо создать таблицу (пункт 2).");
            return;
        }

        switch (code) {
            case 1:
                listTableNames();
                break;
            case 2:
                createTable();
                tableNameCreated = true;
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
                exportToXls();
                selectAllFromTable();
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

    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(100), operand1 TEXT, operand2 TEXT, result TEXT)";
        executeSQL(sql);
        System.out.println("Таблица создана: " + tableName);
    }

    private static String insertStatement() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }

    private static void executeSQL(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listTableNames() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
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
    
    private static void exportToXls() {
        String fileName = "zadacha_two.xls";

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String query = "SELECT 'id', 'operation', 'operand1', 'operand2', 'result' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Данные были сохранены в Excel.");
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("already exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его вручную или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + msg);
            }
        }
    }

    private static void selectAllFromTable() {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-5s | %-15s | %-70s | %-70s | %-150s%n",
                    "ID", "Operation", "Operand1", "Operand2", "Result");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                String operand1 = rs.getString("operand1");
                String operand2 = rs.getString("operand2");
                String result = rs.getString("result");

                System.out.printf("%-5s | %-15s | %-70s | %-70s | %-150s%n",
                        id, operation, operand1, operand2, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}