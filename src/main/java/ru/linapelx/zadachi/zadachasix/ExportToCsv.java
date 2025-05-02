package ru.linapelx.zadachi.zadachasix;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportToCsv extends Command {
    public static void execute(Connection conn, String tableName) {
        String filePath = "src/main/resources/" + tableName + ".csv";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter writer = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                writer.append('"').append(meta.getColumnName(i)).append('"');
                if (i < cols) writer.append(";");
            }
            writer.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    writer.append('"').append(rs.getString(i)).append('"');
                    if (i < cols) writer.append(";");
                }
                writer.append("\n");
            }

            System.out.println("Данные были сохранены в Excel.");
            SelectAllFromTable.execute(conn, tableName, "id", "matrix_name", "row_index", "col_index", "value");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
