package ru.linapelx.zadachi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ZadachaFour {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "root";
    private static final String tableName = "string_operations_table";
    private static boolean tableExists = false;

    private static String string1 = "";
    private static String string2 = "";

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
        tableExists = false;

        System.out.println("Введите первую строку (не менее 50 символов):");
        string1 = readValidatedString();
        System.out.println("Введите вторую строку (не менее 50 символов):");
        string2 = readValidatedString();

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

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Возвращение подстроки по индексам, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("4. Перевод строк в верхний и нижний регистры, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("5. Поиск подстроки и определение окончания подстроки, результат сохранить в MySQL с последующим выводом в консоль.");
        System.out.println("6. Сохранить все данные из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 6 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу!\n");
            return;
        }

        switch (choice) {
            case 1:
                printTables();
                break;
            case 2:
                createTable();
                tableExists = true;
                break;
            case 3:
                System.out.print("Введите начальный индекс: ");
                int start = readInt();
                System.out.print("Введите конечный индекс: ");
                int end = readInt();
                String substr1 = safeSubstring(string1, start, end);
                String substr2 = safeSubstring(string2, start, end);
                String result = "Substring1: " + substr1 + ", Substring2: " + substr2;
                System.out.println(result);
                executeStatement(insertQuery(), "Substring", string1, string2, result);
                break;
            case 4:
                String upperLower = "uppercase1: " + string1.toUpperCase() + ", lowercase1: " + string1.toLowerCase() +
                        ", uppercase2: " + string2.toUpperCase() + ", lowercase2: " + string2.toLowerCase();
                System.out.println(upperLower);
                executeStatement(insertQuery(), "CaseConversion", string1, string2, upperLower);
                break;
            case 5:
                System.out.print("Введите подстроку для поиска: ");
                String substr = scanner.nextLine();
                boolean contains1 = string1.contains(substr);
                boolean ends1 = string1.endsWith(substr);
                boolean contains2 = string2.contains(substr);
                boolean ends2 = string2.endsWith(substr);
                String compare = String.format("%s | str1: contains=%b, endsWith=%b | str2: contains=%b, endsWith=%b",
                        substr, contains1, ends1, contains2, ends2);
                System.out.println(compare);
                executeStatement(insertQuery(), "SubstringSearch", string1, string2, compare);
                break;
            case 6:
                exportToXls();
                selectAllFromTable();
                break;
            case -1:
                System.out.println("Выход из программы.");
                break;
            default:
                System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static String readValidatedString() {
        String input;
        do {
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.println("Ошибка: строка должна содержать не менее 50 символов.");
            }
        } while (input.length() < 50);
        return input;
    }

    private static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Введите целое число: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static String safeSubstring(String input, int start, int end) {
        if (start >= 0 && end <= input.length() && start < end) {
            return input.substring(start, end);
        }
        System.out.println("Недопустимые индексы.");
        return "";
    }

    private static void createTable() {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(255), " +
                "operand1 TEXT, operand2 TEXT, result TEXT)");
        System.out.println("Таблица " + tableName + " успешно создана!");
    }

    private static String insertQuery() {
        return "INSERT INTO " + tableName + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";
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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void printTables() {
        List<String> tables = new ArrayList<>();
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            for (String table : tables) {
                System.out.println(table);
            }
        }
    }

    private static void exportToXls() {
        String fileName = "zadacha_four.xls";

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
}