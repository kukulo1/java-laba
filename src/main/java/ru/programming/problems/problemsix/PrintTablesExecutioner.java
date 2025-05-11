package ru.programming.problems.problemsix;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrintTablesExecutioner extends ProblemSixSolver{
    public static void execute() {
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            if (tables.isEmpty()) {
                System.out.println("Таблицы не найдены.");
            } else {
                tables.forEach(System.out::println);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
