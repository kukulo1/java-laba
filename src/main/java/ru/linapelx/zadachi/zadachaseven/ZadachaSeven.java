package ru.linapelx.zadachi.zadachaseven;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ZadachaSeven {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String tableName = "array_sort_table";
    private static boolean tableExists = false;
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final Scanner scanner = new Scanner(System.in);

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
            case 1:
                printTables();
                break;
            case 2:
                createTable();
                tableExists = true;
                break;
            case 3:
                try {
                    sort = new Sort();
                    sort.inputArray();
                    sort.printArray(sort.array, "исходный массив");
                    saveArrayToDatabase("ishodniy", sort.array);
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода: вводите только целые числа.");
                    scanner.nextLine();
                }
                break;
            case 4:
                if (sort == null) {
                    System.out.println("Сначала введите массив (пункт 3).\n");
                    return;
                }
                int[] asc = sort.sortAscending();
                int[] desc = sort.sortDescending();
                sort.printArray(asc, "По возрастанию");
                sort.printArray(desc, "По убыванию");
                saveArrayToDatabase("vozrastanie", asc);
                saveArrayToDatabase("ubivanie", desc);
                break;
            case 5:
                exportToCsv(tableName, tableName);
                System.out.println("Данные сохранены в Excel.");
                selectAllFromTable(tableName);
                break;
            case -1:
                System.out.println("Выход из программы.");
                break;
            default:
                System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void saveArrayToDatabase(String arrayName, int[] array) {
        String query = "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            executeStatement(query, arrayName, i, array[i]);
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
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printTables() {
        List<String> tables = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
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
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter fw = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                fw.append('"').append(meta.getColumnName(i)).append('"');
                if (i < columnCount) fw.append(";");
            }
            fw.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    fw.append('"');
                    if (value != null) fw.append(value.replace("\"", "\"\""));
                    fw.append('"');
                    if (i < columnCount) fw.append(";");
                }
                fw.append("\n");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void selectAllFromTable(String tableName) {
        try (Connection conn = DriverManager.getConnection(url, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(meta.getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}