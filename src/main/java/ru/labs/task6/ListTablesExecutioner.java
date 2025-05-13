package ru.labs.task6;

import ru.labs.DbHelper;

public class ListTablesExecutioner extends TaskSixRunner {
    public static void execute() {
        DbHelper.listTables();
    }
}
