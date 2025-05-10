package ru.linapelx.zadachi.zadachasix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class ExportToXls {
    public static void execute(Connection conn, String tableName) {
        String fileName = "zadacha_six.xls";

        String filePath = "C:/Users/User/Desktop/" + fileName;

        String query = "SELECT 'id', 'matrix_name', 'row_index', 'col_index', 'value' " +
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
