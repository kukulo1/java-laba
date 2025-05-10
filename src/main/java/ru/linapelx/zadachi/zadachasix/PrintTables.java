package ru.linapelx.zadachi.zadachasix;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrintTables extends ZadachaSix {
    public static void execute(Connection conn) {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (tables.isEmpty()) {
            System.out.println("Таблицы не найдены.");
        } else {
            tables.forEach(System.out::println);
        }
    }
}
