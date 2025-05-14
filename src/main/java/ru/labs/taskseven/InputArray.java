package ru.labs.taskseven;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;

public class InputArray extends TaskSeven {
    public static Sort execute() {
        Sort sort = new Sort();
        try {
            sort.inputArray();
            sort.printArray(sort.array, "исходный массив");

            insertArray(sort.array, "original");

        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите только целые числа.");
            scanner.nextLine();
        }
        return sort;
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
