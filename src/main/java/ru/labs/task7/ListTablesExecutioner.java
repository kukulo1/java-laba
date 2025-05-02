package ru.labs.task7;

import ru.labs.DbHelper;

public class ListTablesExecutioner extends Executioner {
    public static void execute() {
        DbHelper.listTables();
    }
}
