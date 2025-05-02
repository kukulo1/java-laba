package ru.labs.task6;

import ru.labs.DbHelper;

public class ExportToExcelExecutioner extends Executioner {
    static void execute(String tableName) {
        DbHelper.exportToCsv(tableName, tableName);
        System.out.println("Данные были сохранены в Excel.");
        DbHelper.selectAllFromTable(tableName, "id", "matrix_name", "row_index", "col_index", "value");
    }
}
