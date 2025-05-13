package ru.labs.task7;

import ru.labs.DbHelper;

public class ListTablesExecutioner extends TaskSevenRunner{
    public static void execute() {
        DbHelper.listTables();
    }
}
