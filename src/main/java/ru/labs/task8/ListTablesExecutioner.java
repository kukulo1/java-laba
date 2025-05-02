package ru.labs.task8;

import ru.labs.DbHelper;

public class ListTablesExecutioner extends Executioner {
    public static void execute() {
        DbHelper.listTables();
    }
}
