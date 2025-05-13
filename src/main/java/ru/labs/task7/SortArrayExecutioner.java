package ru.labs.task7;

import ru.labs.DbHelper;

public class SortArrayExecutioner extends TaskSevenRunner{
    public static void execute() {
        int[] asc = sort.sortAscending();
        sort.printArray(asc, "Сортировка по возрастанию");
        insertArray(asc, "asc");

        int[] desc = sort.sortDescending();
        sort.printArray(desc, "Сортировка по убыванию");
        insertArray(desc, "desc");
    }

    private static void insertArray(int[] array, String name) {
        String sql = "INSERT INTO " + TABLE_NAME + " (array_name, index_pos, value) VALUES (?, ?, ?)";
        for (int i = 0; i < array.length; i++) {
            DbHelper.execute(sql, name, i, array[i]);
        }
    }
}
