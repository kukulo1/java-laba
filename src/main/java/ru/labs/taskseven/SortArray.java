package ru.labs.taskseven;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SortArray extends TaskSeven {
    public static void execute() {
        if (sort == null) {
            System.out.println("Ошибка: массив не был введён ранее.");
            return;
        }

        int[] asc = sort.sortAscending();
        sort.printArray(asc, "Отсортированный по возрастанию");
        insertArray(asc, "asc");

        int[] desc = sort.sortDescending();
        sort.printArray(desc, "Отсортированный по убыванию");
        insertArray(desc, "desc");
    }

    private static void insertArray(int[] array, String label) {
        String sql = "INSERT INTO " + TABLE_NAME + " (array_name, index_pos, value) VALUES (?, ?, ?)";
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
