package ru.linapelx.zadachi;

import java.sql.*;
import java.util.Scanner;

public class ZadachaThree {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    private static final String tableName = "int_check_task_3_b";
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
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
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
                exportToXls();
                selectAllFromTable();
                break;
            case -1:
                System.out.println("Выход из программы.");
                break;
            default:
                System.out.println("Неверный выбор. Повторите.\n");
        }
    }

    private static void listTables() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
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
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "operation VARCHAR(100), "
                + "operand1 BIGINT, "
                + "result VARCHAR(50))";
        executeUpdate(query);
        System.out.println("Таблица " + tableName + " успешно создана!\n");
    }

    private static void checkNumbers() {
        System.out.println("Введите числа через пробел (для завершения введите 'q'):");
        while (true) {
            String input = scanner.next();
            if (input.equalsIgnoreCase("q")) break;

            try {
                double val = Double.parseDouble(input);
                if (val % 1 != 0) {
                    System.out.println("Ошибка!" + input + " — Онецелое число");
                } else {
                    long longVal = (long) val;
                    if (Double.parseDouble(Long.toString(longVal)) != val) {
                        System.out.println("Ошибка! Введенное число превышает лимит long, попробуйте число поменьше. :3");
                        continue;
                    }
                    boolean isEven = longVal % 2 == 0;
                    String result = isEven ? "Even" : "Odd";
                    System.out.println(longVal + " — целое " + (isEven ? "четное" : "нечетное") + " число");
                    insertIntoTable("Check", longVal, result);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: '" + input + "' не является числом");
            }
        }
    }

    private static void insertIntoTable(String operation, long op1, String result) {
        String sql = "INSERT INTO " + tableName + " (operation, operand1, result) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, operation);
            pstmt.setLong(2, op1);
            pstmt.setString(3, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportToXls() {
        String fileName = "zadacha_three.xls";

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

            System.out.printf("%-5s | %-15s | %-30s | %-20s%n",
                    "ID", "Operation", "Operand1", "Result");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                Double operand1 = rs.getDouble("operand1");
                String result = rs.getString("result");

                System.out.printf("%-5d | %-15s | %-30.2f | %-20s%n",
                        id, operation, operand1, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    private static void executeUpdate(String query) {
        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}