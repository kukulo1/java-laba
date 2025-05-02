package ru.linapelx.zadachi.zadachaseven;

import ru.linapelx.zadachi.zadachaseven.Sort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputArray extends Command {
    public static Sort execute(Connection conn, Scanner scanner, String tableName) {
        Sort sort = new Sort();
        try {
            sort.inputArray();
            sort.printArray(sort.array, "исходный массив");
            saveArray(conn, tableName, "ishodniy", sort.array);
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: введите только целые числа.");
            scanner.nextLine();
        }
        return sort;
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
