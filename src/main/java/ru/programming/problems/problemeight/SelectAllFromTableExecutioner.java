package ru.programming.problems.problemeight;

import java.sql.*;

public class SelectAllFromTableExecutioner extends ProblemEightSolver{
    public static void execute() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            System.out.printf("%-3s | %-40s | %-5s | %-10s\n", "ID", "Имя", "Возраст", "Зарплата");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                double salary = rs.getDouble("salary");

                System.out.printf("%-3d | %-38s | %-7d | %-10.2f\n", id, name, age, salary);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
