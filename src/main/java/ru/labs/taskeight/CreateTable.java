package ru.labs.taskeight;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends Parent {
    public static void execute(Connection conn, String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255), " +
                     "age INT, " +
                     "salary DOUBLE)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица создана успешно.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
