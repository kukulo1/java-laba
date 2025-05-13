package ru.labs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true";
        String user = "root";
        String pass = "root";
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

    public static void exportToXls(String tableName, String fileName) {
        String filePath = "C:/Users/User/Desktop/" + fileName + ".xls";
        String columnList;

        //получаем список колонок из ResultSetMetaData
        try (Statement stmt = connect().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> quotedColumns = new ArrayList<>();
            for (int i = 1; i <= colCount; i++) {
                quotedColumns.add("'" + meta.getColumnName(i) + "'");
            }

            columnList = String.join(", ", quotedColumns);

        } catch (SQLException e) {
            System.out.println("Ошибка при получении структуры таблицы: " + e.getMessage());
            return;
        }

        //SQL-запрос на экспорт в файл .xls
        String exportQuery = "SELECT " + columnList +
                " UNION ALL SELECT * FROM " + tableName +
                " INTO OUTFILE '" + filePath.replace("\\", "/") + "'" +
                " CHARACTER SET cp1251";

        //выполняем экспорт
        try (Statement stmt = connect().createStatement()) {
            stmt.execute(exportQuery);
            System.out.println("Экспорт завершён успешно: " + filePath);
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("already exists")) {
                System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
            } else {
                System.out.println("Ошибка при экспорте: " + msg);
            }
        }
    }


    public static void selectAllFromTable(String tableName, String[] columns, int[] widths) {
        if (columns != null && widths != null && columns.length != widths.length) {
            throw new IllegalArgumentException("Количество колонок и ширин не совпадает.");
        }

        String query = (columns != null && columns.length > 0)
                ? "SELECT " + String.join(", ", columns) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

        try (Statement stmt = connect().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            //заголовки
            for (int i = 1; i <= cols; i++) {
                int width = (widths != null && i <= widths.length) ? widths[i - 1] : 15;
                System.out.printf("| %-" + width + "s", meta.getColumnName(i));
            }
            System.out.println("|");

            //данные
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    int width = (widths != null && i <= widths.length) ? widths[i - 1] : 15;
                    System.out.printf("| %-" + width + "s", rs.getString(i));
                }
                System.out.println("|");
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
