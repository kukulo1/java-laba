package ru.labs;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
        String user = "root";
        String pass = "kukulo1";
        return DriverManager.getConnection(url, user, pass);
    }

    public static void execute(String sql, Object... args) {
        try (PreparedStatement stmt = connect().prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exportToCsv(String tableName, String fileName) {
        String filePath = "src/main/resources/" + fileName + ".csv";
        try (Statement stmt = connect().createStatement();
             FileWriter writer = new FileWriter(filePath)) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                writer.append('"').append(meta.getColumnName(i)).append('"');
                if (i < cols) writer.append(";");
            }
            writer.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    String val = rs.getString(i);
                    writer.append('"');
                    if (val != null) writer.append(val.replace("\"", "\"\""));
                    writer.append('"');
                    if (i < cols) writer.append(";");
                }
                writer.append("\n");
            }

            System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при экспорте данных в CSV.");
        }
    }

    public static void selectAllFromTable(String tableName, String... columns) {
        String query = (columns != null && columns.length > 0)
                ? "SELECT " + String.join(", ", columns) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

        try (Statement stmt = connect().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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

    public static void listTables() {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = connect().createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
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
}
