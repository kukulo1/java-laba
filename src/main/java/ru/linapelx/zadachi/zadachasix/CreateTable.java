package ru.linapelx.zadachi.zadachasix;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends Command {
    public static void execute(Connection conn, String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "matrix_name VARCHAR(255), " +
                     "row_index INT, " +
                     "col_index INT, " +
                     "value DOUBLE)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица " + tableName + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
