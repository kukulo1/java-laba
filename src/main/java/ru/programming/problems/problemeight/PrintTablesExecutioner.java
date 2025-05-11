package ru.programming.problems.problemeight;

import java.sql.*;

public class PrintTablesExecutioner extends ProblemEightSolver{
    public static void execute() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
