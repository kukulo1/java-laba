package ru.labs.task6;

import ru.labs.DbHelper;

public class ExportToExcelExecutioner extends TaskSixRunner {
    static void execute() {
        DbHelper.exportToXls(TABLE_NAME, TABLE_NAME);
        System.out.println("Данные были сохранены в Excel.");
        DbHelper.selectAllFromTable(TABLE_NAME, new String[]{"id", "matrix_name", "row_index", "col_index", "value"}, new int[]{3, 20, 10, 10, 50});
    }
}
