// Обновлённый код для Задачи №4 (строковый тип данных, контрольная работа №1)

package ru.programming.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProblemFourSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_four_table";
    private static boolean tableExists = false;
    private static String str1 = "", str2 = "";

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
        tableExists = false;

        str1 = readValidatedString("Введите первую строку (не менее 50 символов): ");
        str2 = readValidatedString("Введите вторую строку (не менее 50 символов): ");

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

    private static String readValidatedString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.printf("Ошибка: строка должна быть не менее 50 символов (у вас %d)%n", input.length());
            }
        } while (input.length() < 50);
        return input;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Возвращение подстроки по индексам, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Сохранить все данные из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 6 && !tableExists) {
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
                int start = readIndex("Введите начальный индекс подстроки: ");
                int end = readIndex("Введите конечный индекс подстроки: ");
                String substr1 = safeSubstring(str1, start, end);
                String substr2 = safeSubstring(str2, start, end);

                String result = "Substring1: " + substr1 + "; Substring2: " + substr2;
                System.out.println(result);

                executeStatement(insertQuery(), "Substring", str1, str2, result);
            }
            case 4 -> {
                String result = "UP1: " + str1.toUpperCase() + ", LOW1: " + str1.toLowerCase() +
                        ", UP2: " + str2.toUpperCase() + ", LOW2: " + str2.toLowerCase();
                System.out.println(result);
                executeStatement(insertQuery(), "CaseConversion", str1, str2, result);
            }
            case 5 -> {
                System.out.print("Введите подстроку для поиска: ");
                String substr = scanner.nextLine();

                boolean contains1 = str1.contains(substr);
                boolean ends1 = str1.endsWith(substr);
                boolean contains2 = str2.contains(substr);
                boolean ends2 = str2.endsWith(substr);

                String result = String.format(
                        "Substring: %s | str1: contains=%b, endsWith=%b | str2: contains=%b, endsWith=%b",
                        substr, contains1, ends1, contains2, ends2
                );
                System.out.println(result);
                executeStatement(insertQuery(), "SearchAndEnd", str1, str2, result);
            }
            case 6 -> {
                exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(tableName, "id", "operation", "operand1", "operand2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static int readIndex(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } else {
                System.out.println("Введите целое число.");
                scanner.next();
            }
        }
    }

    private static String safeSubstring(String input, int start, int end) {
        if (start >= 0 && end <= input.length() && start < end) {
            return input.substring(start, end);
        } else {
            System.out.println("Недопустимые индексы для подстроки в строке длиной " + input.length());
            return "";
        }
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

    private static void exportToCsv(String tableName, String fileName) {
        String filePath = "src/main/resources/" + fileName + ".csv";
        try (Statement statement = getConnection().createStatement();
             FileWriter fileWriter = new FileWriter(filePath)) {

            String query = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                fileWriter.append('"').append(metaData.getColumnName(i)).append('"');
                if (i < columnCount) fileWriter.append(";");
            }
            fileWriter.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    fileWriter.append('"');
                    if (value != null) {
                        fileWriter.append(value.replace("\"", "\"\""));
                    }
                    fileWriter.append('"');
                    if (i < columnCount) fileWriter.append(";");
                }
                fileWriter.append("\n");
            }

            System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при экспорте данных в CSV.");
        }
    }

    private static void selectAllFromTable(String tableName, String... columnNames) {
        String query = (columnNames != null && columnNames.length > 0)
                ? "SELECT " + String.join(", ", columnNames) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
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
