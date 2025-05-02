package ru.labs.task7;

import ru.labs.DbHelper;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputArrayExecutioner extends Executioner {
    public static void execute(Scanner scanner, String tableName, Sort sort) {
        try {
            sort.inputArray();
            sort.printArray(sort.array, "Исходный массив");
            insertArray(sort.array, "original", tableName);
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только целые числа.");
            scanner.nextLine();
        }
    }

    private static void insertArray(int[] array, String name, String tableName) {
        String sql = "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            DbHelper.execute(sql, name, i, array[i]);
        }
    }
}
