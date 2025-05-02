package ru.linapelx.zadachi.zadachaseven;

import ru.linapelx.zadachi.zadachaseven.Sort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SortArray extends Command {
    public static void execute(Connection conn, String tableName, Sort sort) {
        if (sort == null) {
            System.out.println("Сначала введите массив (пункт 3).\n");
            return;
        }

        int[] asc = sort.sortAscending();
        int[] desc = sort.sortDescending();

        sort.printArray(asc, "По возрастанию");
        sort.printArray(desc, "По убыванию");

        saveArray(conn, tableName, "vozrastanie", asc);
        saveArray(conn, tableName, "ubivanie", desc);
    }

    private static void saveArray(Connection conn, String tableName, String name, int[] array) {
        String sql = "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < array.length; i++) {
                stmt.setString(1, name);
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
