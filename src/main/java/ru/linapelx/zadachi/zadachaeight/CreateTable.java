package ru.linapelx.zadachi.zadachaeight;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable extends ZadachaEight{
    public static void execute(Connection conn, String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255), " +
                     "age INT, " +
                     "salary DOUBLE)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица успешно создана.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
