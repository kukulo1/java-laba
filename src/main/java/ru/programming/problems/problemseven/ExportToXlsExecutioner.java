package ru.programming.problems.problemseven;

import java.sql.*;

public class ExportToXlsExecutioner extends ProblemSevenSolver {
    public static void execute() {
        String filePath = "C:/Users/User/Desktop/problem_seven.xls";

        String query = "SELECT 'id', 'array_name', 'index_pos', 'value' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Таблица была сохранена в Excel.");

            SelectAllFromTableExecutioner.execute();

        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + msg);
            }
        }
    }
}
