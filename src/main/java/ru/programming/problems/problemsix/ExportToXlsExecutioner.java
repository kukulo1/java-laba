package ru.programming.problems.problemsix;

import java.sql.*;

public class ExportToXlsExecutioner extends ProblemSixSolver{
    public static void execute() {
        String filePath = "C:/Users/User/Desktop/problem_six.xls";

        String query = "SELECT 'id', 'matrix_name', 'row_index', 'col_index', 'value' " +
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
            if (msg != null && msg.contains("already exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + msg);
            }
        }
    }
}
