package ru.labs.taskseven;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends Parent {
    public static void execute(Connection conn, String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "array_name VARCHAR(255), " +
                     "index_pos INT, " +
                     "value INT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица " + tableName + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
