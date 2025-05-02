package ru.labs.task7;

import ru.labs.DbHelper;

public class ExportToExcelExecutioner extends Executioner {
    public static void execute(String tableName) {
        DbHelper.exportToCsv(tableName, tableName);
        System.out.println("Данные были сохранены в Excel.");
        DbHelper.selectAllFromTable(tableName, "id", "array_name", "index_pos", "value");
    }
}
