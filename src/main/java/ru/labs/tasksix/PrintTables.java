package ru.labs.tasksix.menu;

import ru.labs.tasksix.Parent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrintTables extends Parent {
    public static void execute(Connection conn) {
        List<String> tables = new ArrayList<>();
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
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
