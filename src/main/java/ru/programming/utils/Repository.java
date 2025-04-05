package ru.programming.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

//это короче класс для работы с БД
//классы с подобным функционалом часто называют Repository, отсюда такое название
public class Repository {
    //это метод для выполнения mysql запросов, Object... это vararg, погуглите чутчут)
    public static boolean executeStatement(String query, Object... params) {
        //блок try-catch используется для выполнения кода, который потенциально может вызвать исключение.
        //т.е. если при выполнении try возникло исключение программа не прерывается, а просто выполняется блок catch
        // погуглите просто про try-catch, а также про try-catch with resources, штука необязательная, но если этот кусок кода останется, то лучше знать что он делает)
        try (PreparedStatement statement = DataBase.getConnection().prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();  // Для INSERT, UPDATE, DELETE
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //метод для получения списка строк с названием таблиц в БД
    public static List<String> getTables() {
        //создается простой список строк
        List<String> tables = new ArrayList<>();
        try (Statement statement = DataBase.getConnection().createStatement()) {
            //resultset - объект, который возвращается при любом вызове методов класса Statement
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            //короче погуглите базово про ResultSet, т.к. без него вы загнетесь на лайв кодинге, если нужно какое-то объяснение - пишите мне
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    //это метод для конвертирования таблиц БД в .csv файл, который можно открыть в excel
    public static void exportToCsv(String tableName, String fileName) {
        //тут задается путь к .csv файлу, куда будет копироваться таблица с названием tableName из БД
        //если файла не существует в директории, то он создасться
        String filePath = "src/main/resources/" + fileName + ".csv";
        try (Statement statement = DataBase.getConnection().createStatement();
             FileWriter fileWriter = new FileWriter(filePath)) {

            String query = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);
            //metadata это короче данные о названии столбцов в таблице, их типе и прочему
            //тоже базово погуглите просто чтобы понимать относительно че тут происходит
            ResultSetMetaData metaData = resultSet.getMetaData();
            //тут допустим берется кол-во столбцов в таблице
            int columnCount = metaData.getColumnCount();

            //тут цикл пробегается по всем названиям колонок в таблице (metaData.getColumnName(i)) и вставляет эти названия в наш .csv файл
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.append('"').append(metaData.getColumnName(i)).append('"');
                //эта строчка нужна чтобы разделять эти названия между собой для нормального парсинга .csv файла экселем
                //если ; не будет между названиями, то при чтении .csv файла эксель не поймет, что эти названия (и другие значения в будущем) нужно разделить в разные ячейки и ебанёт всё в одну ячейку
                if (i < columnCount) fileWriter.append(";");
            }
            //просто переход строки в .csv файле
            fileWriter.append("\n");

            //тут короче вставляются уже не названия колонок, а их содержимое
            //можете попросить  кстати нейросеть подробно объяснить че метод делает, он справится
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    fileWriter.append('"');
                    if (value != null) {
                        fileWriter.append(value.replace("\"", "\"\""));
                    }
                    fileWriter.append('"');
                    if (i < columnCount) fileWriter.append(";");
                }
                fileWriter.append("\n");
            }


            System.out.println("Данные успешно экспортированы в файл CSV: " + filePath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при экспорте данных в CSV.");
        }
    }

    //этот метод нужен для вывода содержимого таблиц в консоль
    //принцип работы тот же, что и при создании .csv файла, только тут содержимое таблицы не в файл записывается, а выводится в консоль
    //если че нейросеть в помощь
    public static void selectAllFromTable(String tableName, String... columnNames) {
        String query;
        if (columnNames != null && columnNames.length > 0) {
            // Собираем список столбцов через запятую
            String columns = String.join(", ", columnNames);
            query = "SELECT " + columns + " FROM " + tableName;
        } else {
            query = "SELECT * FROM " + tableName;
        }

        try (Statement statement = DataBase.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Печатаем заголовки
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Печатаем строки
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
