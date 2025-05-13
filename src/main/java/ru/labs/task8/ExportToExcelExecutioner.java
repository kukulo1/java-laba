package ru.labs.task8;

import ru.labs.DbHelper;

public class ExportToExcelExecutioner extends TaskEightRunner {
    public static void execute() {
        DbHelper.exportToXls(TABLE_NAME, TABLE_NAME);
        DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "name", "age", "salary"}, new int[]{3, 20, 4, 100});
    }
}
