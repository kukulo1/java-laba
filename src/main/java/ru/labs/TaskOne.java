package ru.labs;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskOne {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/database?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "table_one";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";

    private static boolean tableExists = false;

    public static void main(String[] args) {
        executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
        tableExists = false;

        int choice = 0;
        while (choice != -1) {
            showConsoleMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            execute(choice);
        }
    }

    private static Connection connect() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    private static void createTable(String tableName) {
        executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(255), " +
                "operand1 DOUBLE, " +
                "operand2 DOUBLE, " +
                "result DOUBLE)");
        System.out.println("Таблица " + tableName + " успешно создана!");
    }

    private static void showConsoleMenu() {
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

    private static double[] readTwoDoubles() {
        double a = readDouble("Введите первое число: ");
        double b = readDouble("Введите второе число: ");
        return new double[]{a, b};
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 10 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).");
            return;
        }

        switch (choice) {
            case 1 -> printTables();
            case 2 -> {
                createTable(TABLE_NAME);
                tableExists = true;
            }
            case 3 -> {
                double[] operands = readTwoDoubles();
                double result = operands[0] + operands[1];
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "Addition", operands[0], operands[1], result);
            }
            case 4 -> {
                double[] operands = readTwoDoubles();
                double result = operands[0] - operands[1];
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "Subtraction", operands[0], operands[1], result);
            }
            case 5 -> {
                double[] operands = readTwoDoubles();
                double result = operands[0] * operands[1];
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "Multiplication", operands[0], operands[1], result);
            }
            case 6 -> {
                double[] operands = readTwoDoubles();
                if (operands[1] == 0) {
                    System.out.println("Ошибка: деление на ноль.");
                } else {
                    double result = operands[0] / operands[1];
                    System.out.println("Результат: " + result);
                    executeUpdate(INSERT_QUERY, "Division", operands[0], operands[1], result);
                }
            }
            case 7 -> {
                double[] operands = readTwoDoubles();
                if (operands[1] == 0) {
                    System.out.println("Ошибка: деление по модулю на ноль.");
                } else {
                    double result = operands[0] % operands[1];
                    System.out.println("Результат: " + result);
                    executeUpdate(INSERT_QUERY, "Modulus", operands[0], operands[1], result);
                }
            }
            case 8 -> {
                double num = readDouble("Введите число: ");
                double result = Math.abs(num);
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "Absolute", num, null, result);
            }
            case 9 -> {
                double base = readDouble("Введите число: ");
                double power = readDouble("Введите степень: ");
                double result = Math.pow(base, power);
                System.out.println("Результат: " + result);
                executeUpdate(INSERT_QUERY, "Power", base, power, result);
            }
            case 10 -> {
                getExcel(TABLE_NAME, TABLE_NAME);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(TABLE_NAME, "id", "operation", "operand1", "operand2", "result");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    private static void executeUpdate(String query, Object... params) {
        try (PreparedStatement statement = connect().prepareStatement(query)) {
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
        try (Statement statement = connect().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    private static void printTables() {
        List<String> tables = getTables();
        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            for (String s : tables) {
                System.out.println(s);
            }
        }
    }

    private static void getExcel(String tableName, String fileName) {
        String filePath = "src/main/resources/" + fileName + ".csv";
        try (Statement statement = connect().createStatement();
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

        try (Statement statement = connect().createStatement();
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
}
