package ru.labs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskTwo {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/database?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "elis";
    private static Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "table_two";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + " (operation, operand1, operand2, result) VALUES (?, ?, ?, ?)";

    private static String string1;
    private static String string2;
    private static boolean tableExists = false;

    public static void main(String[] args) {
        executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
        tableExists = false;

        int choice;
        do {
            showConsoleMenu();
            while (!scanner.hasNextInt()) {
                System.out.print("Введите корректный номер действия: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();
            execute(choice);
        } while (choice != -1);
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

    private static void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "operation VARCHAR(255), " +
                "operand1 TEXT, " +
                "operand2 TEXT, " +
                "result TEXT)");
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
    }

    private static void showConsoleMenu() {
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
                executeUpdate(INSERT_QUERY, "StringInput", string1, string2, null);
                System.out.println("Строки сохранены:\n1: " + string1 + "\n2: " + string2);
            }
            case 4 -> {
                if (!stringsEntered()) break;
                int length1 = string1.length();
                int length2 = string2.length();
                String result = "Length1: " + length1 + ", Length2: " + length2;
                executeUpdate(INSERT_QUERY, "Length", string1, string2, result);
                System.out.println("Длины строк: " + result);
            }
            case 5 -> {
                if (!stringsEntered()) break;
                String concatenated = string1 + string2;
                executeUpdate(INSERT_QUERY, "Concatenation", string1, string2, concatenated);
                System.out.println("Объединение: " + concatenated);
            }
            case 6 -> {
                if (!stringsEntered()) break;
                String comparison = string1.equals(string2) ? "equal" : "not equal";
                executeUpdate(INSERT_QUERY, "Comparison", string1, string2, comparison);
                System.out.println("Сравнение: " + comparison);
            }
            case 7 -> {
                getExcel();
                selectAllFromTable();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
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

    private static void getExcel() {
        System.out.print("Введите имя файла с расширением (.xls): ");
        String fileName = scanner.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = scanner.nextLine().trim();
        }

        String filePath = "C:/Users/elvin/Desktop/" + fileName;

        String exportQuery =
                "SELECT 'id', 'operation', 'operand1', 'operand2', 'result' " +
                        "UNION ALL " +
                        "SELECT id, operation, operand1, operand2, result " +
                        "FROM " + TABLE_NAME + " " +
                        "INTO OUTFILE '" + filePath + "' " +
                        "CHARACTER SET cp1251";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(exportQuery)) {

            stmt.executeQuery();
            System.out.println("Данные успешно экспортированы в файл: " + filePath);

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
            } else {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
            }
        }
    }

    private static void selectAllFromTable() {
        String query = "SELECT * FROM " + TABLE_NAME;

        try (Statement statement = connect().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Вывод заголовков колонок
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Вывод строк
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
