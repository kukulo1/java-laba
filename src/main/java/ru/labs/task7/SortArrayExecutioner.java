package ru.labs.task7;

import ru.labs.DbHelper;
import ru.labs.task7.Sort;

public class SortArrayExecutioner extends Executioner {
    public static void execute(String tableName, Sort sort) {
        int[] asc = sort.sortAscending();
        sort.printArray(asc, "Сортировка по возрастанию");
        insertArray(asc, "asc", tableName);

        int[] desc = sort.sortDescending();
        sort.printArray(desc, "Сортировка по убыванию");
        insertArray(desc, "desc", tableName);
    }

    private static void insertArray(int[] array, String name, String tableName) {
        String sql = "INSERT INTO " + tableName + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            DbHelper.execute(sql, name, i, array[i]);
        }
    }
}
