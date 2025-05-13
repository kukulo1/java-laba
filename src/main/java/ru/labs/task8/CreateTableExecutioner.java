package ru.labs.task8;

import ru.labs.DbHelper;

public class CreateTableExecutioner extends TaskEightRunner {
    public static void execute() {
        DbHelper.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "age INT, " +
                "salary DOUBLE)");
        System.out.println("Таблица " + TABLE_NAME + " успешно создана!");
    }
}
