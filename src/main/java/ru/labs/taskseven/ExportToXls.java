package ru.labs.taskseven;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ExportToXls extends TaskSeven {
    public static void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя файла с расширением (.xls): ");
        String fileName = scanner.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = scanner.nextLine().trim();
        }

        String filePath = "C:/Users/elvin/Desktop/" + fileName;

        String exportQuery =
                "SELECT " +
                        "'id', 'array_name', 'index_pos', 'value' " +
                        "UNION ALL " +
                        "SELECT * FROM " + TABLE_NAME + " " +
                        "INTO OUTFILE '" + filePath + "' " +
                        "CHARACTER SET cp1251";

        try {
            PreparedStatement stmt = conn.prepareStatement(exportQuery);
            stmt.executeQuery();

            System.out.println("Данные успешно экспортированы в файл: " + filePath);
            SelectAllFromTable.execute();

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Файл уже существует! Удалите его и попробуйте ещё раз.");
            } else {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
            }
        }
    }
}
