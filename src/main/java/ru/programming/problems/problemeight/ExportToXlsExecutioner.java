package ru.programming.problems.problemeight;

import java.sql.*;

public class ExportToXlsExecutioner extends ProblemEightSolver{
    public static void execute() {
        String filePath = "C:/Users/User/Desktop/problem_eight.xls";
        String query = "SELECT 'id', 'name', 'age', 'salary' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Таблица была сохранена в Excel.");
            SelectAllFromTableExecutioner.execute();

        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + errorMessage);
            }
        }
    }
}
