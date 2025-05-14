package ru.labs.taskeight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ExportToXls extends TaskEight {
    public static void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите название файла с расширением (.xls): ");
        String fileName = scanner.nextLine().trim();

        while (!fileName.toLowerCase().endsWith(".xls")) {
            System.out.print("Ошибка: файл должен оканчиваться на .xls. Повторите ввод: ");
            fileName = scanner.nextLine().trim();
        }

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String query1 = "SET SQL_SAFE_UPDATES = 0";
        String query3 = "UPDATE " + TABLE_NAME + " SET salary = ROUND(salary, 2)";
        String query2 = "SET SQL_SAFE_UPDATES = 1";
        String exportQuery =
                "SELECT 'id', 'name', 'age', 'salary' " +
                        "UNION ALL " +
                        "SELECT * FROM " + TABLE_NAME + " " +
                        "INTO OUTFILE '" + filePath + "' " +
                        "CHARACTER SET cp1251";

        try {
            PreparedStatement stmt1 = conn.prepareStatement(query1);
            PreparedStatement stmt3 = conn.prepareStatement(query3);
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            PreparedStatement stmt4 = conn.prepareStatement(exportQuery);

            stmt1.executeUpdate();
            stmt3.executeUpdate();
            stmt2.executeUpdate();
            stmt4.executeQuery();

            System.out.println("Данные успешно экспортированы в файл: " + filePath);

            SelectAllFromTable.execute();

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("Файл уже существует.");
            } else {
                System.out.println("Ошибка при экспорте: " + e.getMessage());
            }
        }
    }
}
