package ru.linapelx.zadachi;

import java.io.FileWriter;
import java.sql.*;
import java.util.Scanner;

public class ZadachaOne {
    static Scanner scanner = new Scanner(System.in);
    static final String DB_URL = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    static final String DB_USER = "root";
    static final String DB_PASS = "kukulo1";
    static final String TABLE = "math_operations_set2";
    static boolean created = false;

    public static void main(String[] args) {
        removeTable();
        menuLoop();
    }

    static void removeTable() {
        runUpdate("DROP TABLE IF EXISTS " + TABLE);
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
        else if (option == 10) exportCsv();
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
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
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
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("SHOW TABLES");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void exportCsv() {
        String file = "src/main/resources/" + TABLE + ".csv";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM " + TABLE);
             FileWriter fw = new FileWriter(file)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                fw.write(meta.getColumnName(i));
                if (i < cols) fw.write(";");
            }
            fw.write("\n");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    fw.write(rs.getString(i));
                    if (i < cols) fw.write(";");
                }
                fw.write("\n");
            }

            System.out.println("Данные сохранены в файл: " + file);
            printTable();

        } catch (Exception e) {
            System.out.println("Ошибка при экспорте.");
        }
    }

    static void printTable() {
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM " + TABLE)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void runUpdate(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
        return "INSERT INTO " + TABLE + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
    }
}
