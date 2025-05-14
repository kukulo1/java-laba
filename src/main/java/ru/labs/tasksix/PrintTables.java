package ru.labs.tasksix;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrintTables extends TaskSix {
    public static void execute() {
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
