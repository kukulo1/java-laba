package ru.labs.task6;

import ru.labs.DbHelper;

public class CreateTableExecutioner extends TaskSixRunner {
    public static void execute() {
        DbHelper.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "matrix_name VARCHAR(255), " +
                "row_index INT, " +
                "col_index INT, " +
                "value DOUBLE)");
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
    }
}
