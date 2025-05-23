package ru.programming.problems.problemseven;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrintTablesExecutioner extends ProblemSevenSolver {
    public static void execute() {
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
