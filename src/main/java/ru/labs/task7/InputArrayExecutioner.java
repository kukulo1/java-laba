package ru.labs.task7;

import ru.labs.DbHelper;

import java.util.InputMismatchException;

public class InputArrayExecutioner extends TaskSevenRunner {
    public static void execute() {
        try {
            sort.inputArray();
            sort.printArray(sort.array, "Исходный массив");
            insertArray(sort.array, "original");
        } catch (InputMismatchException e) {
            System.out.println("Ошибка ввода: необходимо вводить только целые числа.");
            scanner.nextLine();
        }
    }

    private static void insertArray(int[] array, String name) {
        String sql = "INSERT INTO " + TABLE_NAME + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            DbHelper.execute(sql, name, i, array[i]);
        }
    }
}
