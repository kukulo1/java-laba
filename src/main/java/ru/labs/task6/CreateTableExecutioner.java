package ru.labs.task6;

import ru.labs.DbHelper;

public class CreateTableExecutioner extends Executioner {
    public static void execute(String tableName) {
        DbHelper.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "matrix_name VARCHAR(255), " +
                "row_index INT, " +
                "col_index INT, " +
                "value DOUBLE)");
        System.out.println("Таблица " + tableName + " успешно создана!");
    }
}
