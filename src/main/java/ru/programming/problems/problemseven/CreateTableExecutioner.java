package ru.programming.problems.problemseven;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTableExecutioner extends Executioner {
    public static void execute(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "array_name VARCHAR(255), " +
                    "index_pos INT, " +
                    "value INT)");
            System.out.println("Таблица " + tableName + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
