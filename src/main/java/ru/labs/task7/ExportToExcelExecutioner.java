package ru.labs.task7;

import ru.labs.DbHelper;

public class ExportToExcelExecutioner extends TaskSevenRunner{
    public static void execute() {
        DbHelper.exportToXls(TABLE_NAME, TABLE_NAME);
        System.out.println("Данные были сохранены в Excel.");
        DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "array_name", "index_pos", "value"}, new int[]{3, 20, 10, 100});
    }
}
