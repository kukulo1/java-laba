package ru.labs;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskThree {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/database?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Scanner scanner = new Scanner(System.in);
    private static final String TABLE_NAME = "table_three";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + " (operation, operand1, result) VALUES (?, ?, ?)";

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
                "operand1 INT, " +
                "result VARCHAR(255))");
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
    }

    private static void showConsoleMenu() {
        System.out.println("1. Вывести все таблицы из MySQL.");
        System.out.println("2. Создать таблицу в MySQL.");
        System.out.println("3. Проверить числа на целочисленность и чётность, результат сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Сохранить все данные из MySQL в Excel и вывести на экран.");
        System.out.println("Для выхода введите -1.");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 4 && !tableExists) {
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
                System.out.println("Введите числа через пробел (для завершения введите 'q'):");

                while (true) {
                    String input = scanner.next();

                    if (input.equalsIgnoreCase("q")) {
                        break;
                    }

                    if (input.trim().isEmpty()) {
                        System.out.println("Пустой ввод. Повторите.");
                        continue;
                    }

                    try {
                        double number = Double.parseDouble(input);

                        if (Double.isInfinite(number) || Double.isNaN(number)) {
                            System.out.println("Ошибка: значение не является конечным числом.");
                            continue;
                        }

                        if (number % 1 != 0) {
                            System.out.println("Ошибка: " + number + " — нецелое число");
                            continue;
                        }

                        int intVal = (int) number;

                        // проверка возможной потери точности при касте
                        if ((double) intVal != number) {
                            System.out.println("Ошибка: число выходит за пределы типа int.");
                            continue;
                        }

                        boolean even = intVal % 2 == 0;
                        String result = even ? "Even" : "Odd";
                        System.out.println(intVal + " — целое " + (even ? "четное" : "нечетное") + " число");

                        executeUpdate(INSERT_QUERY, "Check", String.valueOf(intVal), result);

                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: '" + input + "' не является числом");
                    }
                }

            }
            case 4 -> {
                getExcel();
                selectAllFromTable();
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
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

    private static void getExcel() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя файла с расширением (.xls): ");
        String fileName = scanner.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = scanner.nextLine().trim();
        }

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String exportQuery =
                "SELECT 'id', 'operation', 'operand1', 'result' " +
                        "UNION ALL " +
                        "SELECT id, operation, operand1, result " +
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
