package ru.programming.problems;

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
                System.out.println("Неверный выбор. Попробуйте снова.");
                printMenu();
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
                createTable();
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
                exportToXls();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> {
                System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static String insertQuery() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }

    private static void createTable() {
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

    private static void exportToXls() {
        String filePath = "C:/Users/User/Desktop/problem_one.xls";

        String query = "SELECT 'id', 'operation', 'operand1', 'operand2', 'result' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Таблица была сохранена в Excel.");
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

            System.out.printf("%-5s | %-15s | %-30s | %-30s | %-200s%n",
                    "ID", "Operation", "Operand1","Operand2", "Result");

            while (rs.next()) {
                int id = rs.getInt("id");
                String operation = rs.getString("operation");
                Double operand1 = rs.getDouble("operand1");
                Double operand2 = rs.getDouble("operand2");
                Double result = rs.getDouble("result");

                System.out.printf("%-5d | %-15s | %-30.2f | %-30.2f | %-30.2f%n",
                        id, operation, operand1, operand2,  result);
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
