package ru.programming.problems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProblemThreeSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    private static final String tableName = "problem_three_table";
    private static boolean tableExists = false;

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
        tableExists = false;

        int choice;
        do {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.println("Неверный выбор. Повторите.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            execute(choice);
        } while (choice != -1);
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 4 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).");
            return;
        }

        switch (choice) {
            case 1 -> printTables();
            case 2 -> {
                createTable();
                tableExists = true;
            }
            case 3 -> {
                System.out.println("Введите числа через пробел (для завершения введите 'q'):");
                while (true) {
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("q")) {
                        break;
                    }
                    try {
                        double number = Double.parseDouble(input);
                        if (number % 1 != 0) {
                            System.out.println("Ошибка: '" + number + "' — нецелое число");
                            continue;
                        }
                        long longVal = (long) number;
                        if (Double.parseDouble(Long.toString(longVal)) != number) {
                            System.out.println("Ошибка! Введенное число превышает лимит long, попробуйте число поменьше.");
                            continue;
                        }
                        boolean even = longVal % 2 == 0;
                        String result = even ? "Even" : "Odd";
                        System.out.println(longVal + " — целое " + (even ? "четное" : "нечетное") + " число");
                        executeStatement(insertQuery(), String.valueOf(longVal), result);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: '" + input + "' не является числом");
                    }
                }
            }
            case 4 -> {
                exportToXls();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> {
                System.out.println("Неверный выбор. Повторите.");

            }
        }
    }

    private static String insertQuery() {
        return "INSERT INTO " + tableName + " (operand1, result) VALUES (?, ?)";
    }

    private static void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "operand1 BIGINT, "
                + "result VARCHAR(50))";
        executeStatement(query);
        System.out.println("Таблица " + tableName + " успешно создана!");
    }

    private static void executeStatement(String query, Object... params) {
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getTables() {
        List<String> tables = new ArrayList<>();
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    private static void exportToXls() {
        String filePath = "C:/Users/User/Desktop/problem_three.xls";

        String query = "SELECT 'id', 'operand1', 'result' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Таблица была сохранена в Excel.");
            selectAllFromTable();
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("already exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + msg);
            }
        }
    }
    private static void selectAllFromTable() {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-5s | %-30s | %-20s%n",
                    "ID", "Operand1", "Result");

            while (rs.next()) {
                int id = rs.getInt("id");
                Double operand1 = rs.getDouble("operand1");
                String result = rs.getString("result");

                System.out.printf("%-5d | %-30.2f | %-20s%n",
                        id, operand1, result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printTables() {
        List<String> tables = getTables();
        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            tables.forEach(System.out::println);
        }
    }
}
