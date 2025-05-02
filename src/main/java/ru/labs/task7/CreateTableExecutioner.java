package ru.labs.task7;

import ru.labs.DbHelper;

public class CreateTableExecutioner extends Executioner {
    public static void execute(String tableName) {
        DbHelper.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "array_name VARCHAR(255), " +
                "index_pos INT, " +
                "value INT)");
        System.out.println("Таблица " + tableName + " успешно создана!");
    }
}
