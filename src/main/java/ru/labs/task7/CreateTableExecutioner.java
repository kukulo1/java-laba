package ru.labs.task7;

import ru.labs.DbHelper;

public class CreateTableExecutioner extends TaskSevenRunner{
    public static void execute() {
        DbHelper.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "array_name VARCHAR(255), " +
                "index_pos INT, " +
                "value INT)");
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
    }
}
