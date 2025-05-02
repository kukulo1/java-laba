package ru.programming.problems.problemseven;

import ru.programming.problems.problemseven.Sort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputArrayExecutioner extends Executioner {
    public static Sort execute(Connection conn, Scanner scanner, String tableName) {
        Sort sort = new Sort();
        try {
            sort.inputArray();
            sort.printArray(sort.array, "исходный массив");
            insertArray(conn, sort.array, tableName, "original");
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только целые числа.");
            scanner.nextLine();
        }
        return sort;
    }

    private static void insertArray(Connection conn, int[] array, String tableName, String label) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)")) {
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
