package ru.linapelx.zadachi.zadachaseven;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportToCsv extends Command {
    public static void execute(Connection conn, String tableName) {
        String filePath = "src/main/resources/" + tableName + ".csv";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter fw = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();

            for (int i = 1; i <= count; i++) {
                fw.append('"').append(meta.getColumnName(i)).append('"');
                if (i < count) fw.append(";");
            }
            fw.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    fw.append('"').append(rs.getString(i)).append('"');
                    if (i < count) fw.append(";");
                }
                fw.append("\n");
            }

            System.out.println("Данные сохранены в Excel.");
            SelectAllFromTable.execute(conn, tableName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
