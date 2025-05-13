package ru.labs.task8;

import ru.labs.DbHelper;

public class ListTablesExecutioner extends TaskEightRunner  {
    public static void execute() {
        DbHelper.listTables();
    }
}
