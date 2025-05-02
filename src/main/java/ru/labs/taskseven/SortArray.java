package ru.labs.taskseven;

import ru.labs.taskseven.Sort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SortArray extends Parent {
    public static void execute(Connection conn, Sort sort, String tableName) {
        if (sort == null) {
            System.out.println("Ошибка: массив не был введён ранее.");
            return;
        }

        int[] asc = sort.sortAscending();
        sort.printArray(asc, "Отсортированный по возрастанию");
        insertArray(conn, asc, tableName, "asc");

        int[] desc = sort.sortDescending();
        sort.printArray(desc, "Отсортированный по убыванию");
        insertArray(conn, desc, tableName, "desc");
    }

    private static void insertArray(Connection conn, int[] array, String tableName, String label) {
        String sql = "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < array.length; i++) {
                stmt.setString(1, label);
                stmt.setInt(2, i);
                stmt.setInt(3, array[i]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
