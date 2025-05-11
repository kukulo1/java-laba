package ru.programming.problems.problemeight;

import java.sql.Statement;

public class CreateTableExecutioner extends ProblemEightSolver{
    public static void execute() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "age INT, " +
                    "salary DOUBLE)");
            System.out.println("Таблица создана успешно.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
