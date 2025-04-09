package ru.programming.problems;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProblemOneSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_one_table";

    private static boolean tableExists = false;

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
            execute(choice);
        } while (choice != -1);
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Сложение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("4. Вычитание чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("5. Умножение чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("6. Деление чисел, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("7. Деление чисел по модулю (остаток), результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("8. Возведение числа в модуль, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("9. Возведение числа в степень, результат сохранить в MySQL с последующим выводом в консоль");
        System.out.println("10. Сохранить все данные (вышеполученные результаты) из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Ошибка ввода. Повторите: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 10 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).");
            return;
        }

        switch (choice) {
            case 1 -> printTables();
            case 2 -> {
                createTable(tableName);
                tableExists = true;
            }
            case 3 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                double result = a + b;
                System.out.println("Результат: " + result);
                executeStatement(insertQuery(), "Addition", a, b, result);
            }
            case 4 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                double result = a - b;
                System.out.println("Результат: " + result);
                executeStatement(insertQuery(), "Subtraction", a, b, result);
            }
            case 5 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                double result = a * b;
                System.out.println("Результат: " + result);
                executeStatement(insertQuery(), "Multiplication", a, b, result);
            }
            case 6 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                if (b == 0) {
                    System.out.println("Ошибка: деление на ноль.");
                } else {
                    double result = a / b;
                    System.out.println("Результат: " + result);
                    executeStatement(insertQuery(), "Division", a, b, result);
                }
            }
            case 7 -> {
                double a = readDouble("Введите первое число: ");
                double b = readDouble("Введите второе число: ");
                if (b == 0) {
                    System.out.println("Ошибка: деление по модулю на ноль.");
                } else {
                    double result = a % b;
                    System.out.println("Результат: " + result);
                    executeStatement(insertQuery(), "Modulus", a, b, result);
                }
            }
            case 8 -> {
                double num = readDouble("Введите число: ");
                double result = Math.abs(num);
                System.out.println("Результат: " + result);
                executeStatement(insertQuery(), "Absolute", num, null, result);
            }
            case 9 -> {
                double base = readDouble("Введите число: ");
                double power = readDouble("Введите степень: ");
                double result = Math.pow(base, power);
                System.out.println("Результат: " + result);
                executeStatement(insertQuery(), "Power", base, power, result);
            }
            case 10 -> {
                exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(tableName, "id", "operation", "operand1", "operand2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private static String insertQuery() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }

    private static void createTable(String tableName) {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(255), " +
                "operand1 DOUBLE, " +
                "operand2 DOUBLE, " +
                "result DOUBLE)");
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
        String query;
        if (columnNames != null && columnNames.length > 0) {
            String columns = String.join(", ", columnNames);
            query = "SELECT " + columns + " FROM " + tableName;
        } else {
            query = "SELECT * FROM " + tableName;
        }

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
