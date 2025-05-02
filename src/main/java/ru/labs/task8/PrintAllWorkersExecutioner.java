package ru.labs.task8;

import ru.labs.DbHelper;

public class PrintAllWorkersExecutioner extends Executioner {
    public static void execute(String tableName) {
        DbHelper.selectAllFromTable(tableName, "id", "name", "age", "salary");
    }
}
