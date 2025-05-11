package ru.programming.problems.problemsix;

import java.sql.SQLException;
import java.sql.Statement;

public class CreateTableExecutioner extends ProblemSixSolver{
    public static void execute() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "matrix_name VARCHAR(255), " +
                    "row_index INT, " +
                    "col_index INT, " +
                    "value BIGINT)");
            System.out.println("Таблица " + tableName + " успешно создана!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
