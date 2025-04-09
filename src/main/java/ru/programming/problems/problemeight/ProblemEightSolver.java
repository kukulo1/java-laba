package ru.programming.problems.problemeight;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

public class ProblemEightSolver {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
    private static final String username = "root";
    private static final String password = "kukulo1";
    private static final String tableName = "problem_eight_table";
    private static final List<Worker> workers = new ArrayList<>();
    private static boolean tableExists = false;

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);

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
        System.out.println("3. Ввод значений ВСЕХ полей, сохранить их в MySQL с выводом в консоль.");
        System.out.println("4. Сохранение всех результатов из MySQL с последующим выводом в консоль.");
        System.out.println("5. Сохранить результаты из MySQL в Excel и вывести их в консоль.");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 5 && !tableExists) {
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
                Worker worker = new Worker();
                System.out.print("Введите имя студента: ");
                worker.setName(scanner.nextLine());

                int age = -1;
                while (age < 0 || age > 100) {
                    System.out.print("Введите возраст студента (0-100): ");
                    while (!scanner.hasNextInt()) {
                        System.out.print("Ошибка. Введите целое число: ");
                        scanner.next();
                    }
                    age = scanner.nextInt();
                }
                worker.setAge(age);

                double salary = -1;
                while (salary < 0) {
                    System.out.print("Введите зарплату студента (неотрицательное число): ");
                    while (!scanner.hasNextDouble()) {
                        System.out.print("Ошибка. Введите число: ");
                        scanner.next();
                    }
                    salary = scanner.nextDouble();
                }
                worker.setSalary(salary);
                scanner.nextLine();

                workers.add(worker);
                executeStatement("INSERT INTO " + tableName + " (name, age, salary) VALUES (?, ?, ?)",
                        worker.getName(), worker.getAge(), worker.getSalary());

                System.out.printf("Добавлен: %s, %d лет, зарплата %.2f\n",
                        worker.getName(), worker.getAge(), worker.getSalary());
            }
            case 4 -> selectAllFromTable(tableName);
            case 5 -> {
                exportToCsv(tableName, tableName);
                selectAllFromTable(tableName);
            }
        }
    }

    private static void createTable() {
        executeStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "age INT, " +
                "salary DOUBLE)");
        System.out.println("Таблица создана успешно.");
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
        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

            System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);

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
