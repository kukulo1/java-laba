package ru.programming.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProblemFiveSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_five_table";
    private static boolean tableExists = false;

    private static StringBuffer str1 = new StringBuffer();
    private static StringBuffer str2 = new StringBuffer();

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
        tableExists = false;

        str1 = new StringBuffer(readValidatedString("Введите первую строку (не менее 50 символов): "));
        str2 = new StringBuffer(readValidatedString("Введите вторую строку (не менее 50 символов): "));

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
        System.out.println("3. изменить порядок символов строк на обратный, сохранить в MySQL и вывести в консоль");
        System.out.println("4. Добавить одну строку в другую, сохранить в MySQL и вывести в консоль");
        System.out.println("5. Сохранить все данные из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 5 && !tableExists) {
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
                String reversed1 = new StringBuffer(str1).reverse().toString();
                String reversed2 = new StringBuffer(str2).reverse().toString();

                System.out.println("Реверс первой строки: " + reversed1);
                System.out.println("Реверс второй строки: " + reversed2);

                String result = "Reversed1: " + reversed1 + "; Reversed2: " + reversed2;
                executeStatement(insertQuery(), "Reverse", str1.toString(), str2.toString(), result);
            }
            case 4 -> {
                String before = str1.toString();
                str1.append(str2);
                String result = "Result after append: " + str1;

                System.out.println(result);
                executeStatement(insertQuery(), "Append", before, str2.toString(), str1.toString());
            }
            case 5 -> {
                exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(tableName, "id", "operation", "operand1", "operand2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
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
