package ru.linapelx.zadachi.zadachasix;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ZadachaSix {
    private static Matrix matrix;
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String tableName = "matrix_data_table";
    private static boolean tableExists = false;
    private static final Scanner scanner = new Scanner(System.in);


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
        System.out.println("3. Ввести две матрицы с клавиатуры и сохранить их в MySQL.");
        System.out.println("4. Перемножить матрицы, сохранить результат в MySQL и вывести в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести в консоль.");
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
                createTable(tableName);
                tableExists = true;
            }
            case 3 -> {
                try {
                    matrix = new Matrix();
                    matrix.inputMatricesFromKeyboard();
                    matrix.printMatrix(matrix.arrayA, "Первая матрица");
                    matrix.printMatrix(matrix.arrayB, "Вторая матрица");
                    insertMatrixIntoDatabase(matrix.arrayA, "matrix_1");
                    insertMatrixIntoDatabase(matrix.arrayB, "matrix_2");
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода: необходимо вводить только числа. Операция прервана.");
                    scanner.nextLine();
                }
            }
            case 4 -> {
                if (matrix != null) {
                    int[][] result = matrix.multiplyMatrices();
                    matrix.printMatrix(result, "Результат (A x B)");
                    insertMatrixIntoDatabase(result, "result_matrix");
                } else {
                    System.out.println("Ошибка: матрицы не были введены.");
                }
            }
            case 5 -> {
                exportToCsv(tableName, tableName);
                System.out.println("Данные были сохранены в Excel.");
                selectAllFromTable(tableName, "id", "matrix_name", "row_index", "col_index", "value");
            }
            case -1 -> System.out.println("Выход из программы.");
            default -> System.out.println("Неверный выбор. Повторите.");
        }
    }

    private static void insertMatrixIntoDatabase(int[][] matrixData, String matrixName) {
        String query = "INSERT INTO " + tableName + " (matrix_name, row_index, col_index, value) VALUES (?, ?, ?, ?)";
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                executeStatement(query, matrixName, i, j, matrixData[i][j]);
            }
        }
    }

    private static void createTable(String tableName) {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "matrix_name VARCHAR(255), " +
                "row_index INT, " +
                "col_index INT, " +
                "value DOUBLE)");
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
        return DriverManager.getConnection(url, USERNAME, PASSWORD);
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
                    if (value != null) {
                        fileWriter.append(value.replace("\"", "\"\""));
                    }
                    fileWriter.append('"');
                    if (i < columnCount) fileWriter.append(";");
                }
                fileWriter.append("\n");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при экспорте данных в CSV.");
        }
    }

    private static void selectAllFromTable(String tableName, String... columnNames) {
        String query = (columnNames != null && columnNames.length > 0)
                ? "SELECT " + String.join(", ", columnNames) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

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
}
