package ru.linapelx.zadachi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ZadachaFive {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/java_labs?createDatabaseIfNotExist=true";
    private static final String PASSWORD = "root";
    private static boolean tableExists = false;
    private static final String tableName = "stringbuffer_operations_table";
    private static final String USERNAME = "root";

    private static StringBuffer str1 = new StringBuffer();
    private static StringBuffer str2 = new StringBuffer();

    public static void main(String[] args) {
        executeStatement("DROP TABLE IF EXISTS " + tableName);
        tableExists = false;

        str1 = new StringBuffer(readValidatedString("Введите первую строку (не менее 50 символов): "));
        str2 = new StringBuffer(readValidatedString("Введите вторую строку (не менее 50 символов): "));

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
        System.out.println("1. Вывести все таблицы из MySQL");
        System.out.println("2. Создать таблицу в MySQL");
        System.out.println("3. изменить порядок символов строк на обратный, сохранить в MySQL и вывести в консоль");
        System.out.println("4. Добавить одну строку в другую, сохранить в MySQL и вывести в консоль");
        System.out.println("5. Сохранить все данные из MySQL в Excel и вывести на экран");
        System.out.println("Для выхода введите -1");
        System.out.print("Выберите действие: ");
    }

    private static void execute(int choice) {
        if (choice >= 3 && choice <= 5 && !tableExists) {
            System.out.println("Ошибка: сначала создайте таблицу\n");
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
                String reversed1 = new StringBuffer(str1).reverse().toString();
                String reversed2 = new StringBuffer(str2).reverse().toString();
                System.out.println("Реверс первой строки: " + reversed1);
                System.out.println("Реверс второй строки: " + reversed2);
                String reverseResult = "Reversed1: " + reversed1 + "; Reversed2: " + reversed2;
                executeStatement(insertQuery(), "Reverse", str1.toString(), str2.toString(), reverseResult);
                break;
            case 4:
                String beforeAppend = str1.toString();
                str1.append(str2);
                String appendResult = "After append: " + str1.toString();
                System.out.println(appendResult);
                executeStatement(insertQuery(), "Append", beforeAppend, str2.toString(), str1.toString());
                break;
            case 5:
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

    private static String readValidatedString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
            if (input.length() < 50) {
                System.out.printf("Ошибка: строка должна быть не менее 50 символов (у вас %d)%n", input.length());
            }
        } while (input.length() < 50);
        return input;
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

    private static void exportToXls() {
        String fileName = "zadacha_five.xls";

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
