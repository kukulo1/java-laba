package ru.programming.problems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProblemTwoSolver {
    private static String string1;
    private static String string2;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_two_table";
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
            scanner.nextLine(); // поглощаем перевод строки
            execute(choice);
        } while (choice != -1);
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Ввести две строки с клавиатуры, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Подсчитать размер ранее введенных строк, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("5. Объединить две строки в единое целое, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("6. Сравнить две ранее введенные строки, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("7. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 7 && !tableExists) {
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
                string1 = readString("Введите первую строку (не менее 50 символов): ");
                string2 = readString("Введите вторую строку (не менее 50 символов): ");

                executeStatement(insertQuery(), "StringInput", string1, string2, null);

                System.out.println("Строки сохранены:");
                System.out.println("1: " + string1);
                System.out.println("2: " + string2);
            }
            case 4 -> {
                if (!stringsEntered()) break;
                int length1 = string1.length();
                int length2 = string2.length();
                String result = "Length1: " + length1 + ", Length2: " + length2;
                executeStatement(insertQuery(), "Length", string1, string2, result);
                System.out.println("Длины строк: " + result);
            }
            case 5 -> {
                if (!stringsEntered()) break;
                String concatenated = string1 + string2;
                executeStatement(insertQuery(), "Concatenation", string1, string2, concatenated);
                System.out.println("Объединение: " + concatenated);
            }
            case 6 -> {
                if (!stringsEntered()) break;
                String comparison = string1.equals(string2) ? "equal" : "not equal";
                executeStatement(insertQuery(), "Comparison", string1, string2, comparison);
                System.out.println("Сравнение: " + comparison);
            }
            case 7 -> {
                exportToXls();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> {
                System.out.println("Неверный выбор. Повторите.");
            }
        }
    }

    private static boolean stringsEntered() {
        if (string1 == null || string2 == null) {
            System.out.println("Сначала введите строки (пункт 3).");
            return false;
        }
        return true;
    }

    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.println("Строка слишком короткая. Нужно не менее 50 символов.");
            }
        } while (input.length() < 50);
        return input;
    }

    private static String insertQuery() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }

    private static void createTable() {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(255), " +
                "operand1 TEXT, " +
                "operand2 TEXT, " +
                "result TEXT)");
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
        String filePath = "C:/Users/User/Desktop/problem_two.xls";

        String query = "SELECT 'id', 'operation', 'operand1', 'operand2', 'result' " +
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

    private static void printTables() {
        List<String> tables = getTables();
        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            tables.forEach(System.out::println);
        }
    }
}