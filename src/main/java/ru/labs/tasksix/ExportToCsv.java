package ru.labs.tasksix;

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
                    fw.append('"').append(rs.getString(i)).append('"');
                    if (i < colCount) fw.append(";");
                }
                fw.append("\n");
            }

            System.out.println("Данные были сохранены в Excel.");
            SelectAllFromTable.execute(conn, tableName, "id", "matrix_name", "row_index", "col_index", "value");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
