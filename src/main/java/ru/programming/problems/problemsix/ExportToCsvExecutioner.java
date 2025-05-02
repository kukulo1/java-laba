package ru.programming.problems.problemsix;

import ru.programming.problems.problemseven.SelectAllFromTableExecutioner;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportToCsvExecutioner extends Executioner {
    public static void execute(ConnectionProvider provider, String tableName) {
        String filePath = "src/main/resources/" + tableName + ".csv";

        try (Statement stmt = provider.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             FileWriter fw = new FileWriter(filePath)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                fw.append(meta.getColumnName(i));
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
            SelectAllFromTableExecutioner.execute("problem_six_table", "id", "matrix_name", "row_index", "col_index", "value");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
