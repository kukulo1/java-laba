package ru.labs.task8;

import ru.labs.DbHelper;

public class PrintAllWorkersExecutioner extends TaskEightRunner {
    public static void execute() {
        DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "name", "age", "salary"}, new int[]{3, 20, 4, 100});
    }
}
