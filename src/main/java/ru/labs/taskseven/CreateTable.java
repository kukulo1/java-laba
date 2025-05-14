package ru.labs.taskseven;


import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends TaskSeven {
    public static void execute() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "array_name VARCHAR(255), " +
                     "index_pos INT, " +
                     "value BIGINT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
