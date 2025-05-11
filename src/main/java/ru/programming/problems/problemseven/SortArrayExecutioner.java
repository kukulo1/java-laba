package ru.programming.problems.problemseven;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SortArrayExecutioner extends ProblemSevenSolver{
    public static void execute() {
        if (sort == null) {
            System.out.println("Ошибка: массив не был введён ранее.");
            return;
        }

        long[] asc = sort.sortAscending();
        sort.printArray(asc, "Отсортированный по возрастанию");
        insertArray(conn, asc, tableName, "asc");

        long[] desc = sort.sortDescending();
        sort.printArray(desc, "Отсортированный по убыванию");
        insertArray(conn, desc, tableName, "desc");
    }

    private static void insertArray(Connection conn, long[] array, String tableName, String label) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)")) {
            for (int i = 0; i < array.length; i++) {
                stmt.setString(1, label);
                stmt.setInt(2, i);
                stmt.setLong(3, array[i]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
