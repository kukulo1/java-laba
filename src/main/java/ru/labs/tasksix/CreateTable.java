package ru.labs.tasksix;

import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends TaskSix {
    public static void execute() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "matrix_name VARCHAR(255), " +
                    "row_index INT, " +
                    "col_index INT, " +
                    "value DOUBLE)");
            System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
