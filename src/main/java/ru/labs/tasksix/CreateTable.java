package ru.labs.tasksix.menu;

import ru.labs.tasksix.Parent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends Parent {
    public static void execute(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "matrix_name VARCHAR(255), " +
                    "row_index INT, " +
                    "col_index INT, " +
                    "value DOUBLE)");
            System.out.println("Таблица " + tableName + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
