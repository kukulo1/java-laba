package ru.linapelx.zadachi.zadachaeight;

import java.sql.*;
import java.util.Scanner;

public class ExportToXls extends ZadachaEight{
    public static void execute(Connection conn, String tableName) {
        String fileName = "zadacha_eight.xls";

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String query = "SELECT 'id', 'name', 'age', 'salary' " +
                "UNION ALL " +
                "SELECT * FROM " + tableName + " " +
                "INTO OUTFILE '" + filePath + "' " +
                "CHARACTER SET cp1251";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeQuery();
            System.out.println("Данные были сохранены в Excel.");

            SelectAllFromTable.execute(conn, tableName);

        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("already exists")) {
                System.out.println("Ошибка: файл уже существует. Удалите его вручную или выберите другое имя.");
            } else {
                System.out.println("Ошибка при сохранении в Excel: " + msg);
            }
        }
    }
}
