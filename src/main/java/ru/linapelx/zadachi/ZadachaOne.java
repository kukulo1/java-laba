package ru.linapelx.zadachi;

import java.sql.*;
import java.util.Scanner;

public class ZadachaOne {
    static Scanner scanner = new Scanner(System.in);
    static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    static final String username = "root";
    static final String password = "root";
    static final String tableName = "math_operations_set2";
    static boolean created = false;

    public static void main(String[] args) {
        removeTable();
        menuLoop();
    }

    static void removeTable() {
        runUpdate("DROP TABLE IF EXISTS " + tableName);
    }

    static void menuLoop() {
        int input;
        do {
            printMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            input = scanner.nextInt();
            scanner.nextLine();
            handleInput(input);
        } while (input != -1);
    }

    static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. Сложение чисел");
        System.out.println("4. Вычитание чисел");
        System.out.println("5. Умножение чисел");
        System.out.println("6. Деление чисел");
        System.out.println("7. Деление по модулю");
        System.out.println("8. Модуль числа");
        System.out.println("9. Возведение в степень");
        System.out.println("10. Сохранить данные в CSV и вывести на экран");
        System.out.println("-1. Выход");
        System.out.print("Выберите действие: ");
    }

    static void handleInput(int option) {
        if (option >= 3 && option <= 10 && !created) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2).");
            return;
        }

        if (option == 1) showTables();
        else if (option == 2) createTable();
        else if (option == 3) mathOperation("Addition", '+');
        else if (option == 4) mathOperation("Subtraction", '-');
        else if (option == 5) mathOperation("Multiplication", '*');
        else if (option == 6) mathOperation("Division", '/');
        else if (option == 7) mathOperation("Modulus", '%');
        else if (option == 8) modulus();
        else if (option == 9) power();
        else if (option == 10) {
            exportToXls();
            selectAllFromTable();
        }
        else if (option == -1) System.out.println("Выход из программы.");
        else System.out.println("Неверный выбор.");
    }

    static double getDouble(String msg) {
        System.out.print(msg);
        while (!scanner.hasNextDouble()) {
            System.out.print("Ошибка ввода. Повторите: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    static void mathOperation(String label, char op) {
        double x = getDouble("Введите первое число: ");
        double y = getDouble("Введите второе число: ");
        double result = 0;

        if ((op == '/' || op == '%') && y == 0) {
            System.out.println("Ошибка: деление на ноль.");
            return;
        }

        if (op == '+') result = x + y;
        else if (op == '-') result = x - y;
        else if (op == '*') result = x * y;
        else if (op == '/') result = x / y;
        else if (op == '%') result = x % y;

        System.out.println("Результат: " + result);
        runUpdate(insertStatement(), label, x, y, result);
    }

    static void modulus() {
        double value = getDouble("Введите число: ");
        double result = Math.abs(value);
        System.out.println("Результат: " + result);
        runUpdate(insertStatement(), "Absolute", value, null, result);
    }

    static void power() {
        double base = getDouble("Введите число: ");
        double exp = getDouble("Введите степень: ");
        double result = Math.pow(base, exp);
        System.out.println("Результат: " + result);
        runUpdate(insertStatement(), "Power", base, exp, result);
    }

    static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "operation VARCHAR(255)," +
                "operand1 DOUBLE," +
                "operand2 DOUBLE," +
                "result DOUBLE)";
        runUpdate(sql);
        created = true;
        System.out.println("Таблица создана.");
    }

    static void showTables() {
        try (Connection c = DriverManager.getConnection(url, username, password);
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportToXls() {
        String fileName = "zadacha_one.xls";

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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    static void runUpdate(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static String insertStatement() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }
}
