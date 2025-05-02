package ru.programming.problems.problemsix;

import ru.programming.problems.problemseven.Executioner;

import java.sql.*;

public class SelectAllFromTableExecutioner extends Executioner {
    public static void execute(String tableName, String... columns) {
        String query = (columns != null && columns.length > 0)
                ? "SELECT " + String.join(", ", columns) + " FROM " + tableName
                : "SELECT * FROM " + tableName;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
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

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/my_db?createDatabaseIfNotExist=true",
                "root",
                "kukulo1"
        );
    }
}
