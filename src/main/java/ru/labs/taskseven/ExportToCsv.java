package ru.labs.taskseven;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportToCsv extends Parent {
    public static void execute(Connection conn, String tableName) {
        String path = "src/main/resources/" + tableName + ".csv";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter writer = new FileWriter(path)) {

            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();

            for (int i = 1; i <= count; i++) {
                writer.append('"').append(meta.getColumnName(i)).append('"');
                if (i < count) writer.append(";");
            }
            writer.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    writer.append('"').append(rs.getString(i)).append('"');
                    if (i < count) writer.append(";");
                }
                writer.append("\n");
            }

            System.out.println("Данные были сохранены в Excel.");

            SelectAllFromTable.execute(conn, tableName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
