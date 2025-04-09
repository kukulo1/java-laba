package ru.programming.problems.problemseven;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProblemSevenSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_seven_table";
    private static boolean tableExists = false;

    private static Sort sort;

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
            scanner.nextLine();
            execute(choice);
        } while (choice != -1);
    }

    private static void printMenu() {
        System.out.println("1. Вывести все таблицы из базы данных MySQL.");
        System.out.println("2. Создать таблицу в базе данных MySQL.");
        System.out.println("3. Ввести массив и сохранить в MySQL с выводом в консоль.");
        System.out.println("4. Отсортировать массив, сохранить в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 5 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу (пункт 2 в меню).\n");
            return;
        }

        switch (choice) {
            case 1 -> printTables();
            case 2 -> {
                createTable();
                tableExists = true;
            }
            case 3 -> {
                try {
                    sort = new Sort();
                    sort.inputArray();
                    sort.printArray(sort.array, "исходный массив");
                    for (int i = 0; i < sort.array.length; i++) {
                        executeStatement("INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)",
                                "original", i, sort.array[i]);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода: необходимо вводить только целые числа. Операция прервана.");
                    scanner.nextLine();
                }
            }
            case 4 -> {
                if (sort != null) {
                    int[] asc = sort.sortAscending();
                    sort.printArray(asc, "Отсортированный по возрастанию");
                    for (int i = 0; i < asc.length; i++) {
                        executeStatement("INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)",
                                "asc", i, asc[i]);
                    }
                    int[] desc = sort.sortDescending();
                    sort.printArray(desc, "Отсортированный по убыванию");
                    for (int i = 0; i < desc.length; i++) {
                        executeStatement("INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)",
                                "desc", i, desc[i]);
                    }
                } else {
                    System.out.println("Ошибка: массив не был введён ранее.");
                }
            }
            case 5 -> {
                exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(tableName);
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void createTable() {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "array_name VARCHAR(255), " +
                "index_pos INT, " +
                "value INT)");
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
            tables.forEach(System.out::println);
        }
    }

    private static void exportToCsv(String tableName, String fileName) {
        String filePath = "src/main/resources/" + fileName + ".csv";
        try (Statement statement = getConnection().createStatement();
             FileWriter fileWriter = new FileWriter(filePath)) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
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
                    if (value != null) fileWriter.append(value.replace("\"", "\"\""));
                    fileWriter.append('"');
                    if (i < columnCount) fileWriter.append(";");
                }
                fileWriter.append("\n");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void selectAllFromTable(String tableName) {
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {

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
