package ru.programming.problems.problemseven;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InputArrayExecutioner extends ProblemSevenSolver {
    public static Sort execute() {
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
