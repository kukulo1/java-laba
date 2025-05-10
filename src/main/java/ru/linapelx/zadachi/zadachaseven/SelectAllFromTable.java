package ru.linapelx.zadachi.zadachaseven;

import java.sql.*;

public class SelectAllFromTable extends ZadachaSeven {
    public static void execute(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-3s | %-20s | %-10s | %-20s\n", "ID", "Имя массива", "Индекс", "Значение");

            while (rs.next()) {
                int id = rs.getInt("id");
                String arrayName = rs.getString("array_name");
                int index = rs.getInt("index_pos");
                long value = rs.getLong("value");

                System.out.printf("%-3d | %-20s | %-10d | %-20d\n", id, arrayName, index, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
