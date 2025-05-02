package ru.labs.taskeight;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportToCsv extends Parent {
    public static void execute(Connection conn, String tableName) {
        String filePath = "src/main/resources/" + tableName + ".csv";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter fw = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                fw.append('"').append(meta.getColumnName(i)).append('"');
                if (i < colCount) fw.append(";");
            }
            fw.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    String value = rs.getString(i);
                    fw.append('"');
                    if (value != null) {
                        fw.append(value.replace("\"", "\"\""));
                    }
                    fw.append('"');
                    if (i < colCount) fw.append(";");
                }
                fw.append("\n");
            }

            System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);

            SelectAllFromTable.execute(conn, tableName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
